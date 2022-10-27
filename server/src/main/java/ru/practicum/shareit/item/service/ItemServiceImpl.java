package ru.practicum.shareit.item.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableForUserException;
import ru.practicum.shareit.exception.UpdateFailedException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentStorage;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoInfo;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.service.UserServiceImpl.USER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemStorage storage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final CommentStorage commentStorage;
    private final ItemRequestStorage itemRequestStorage;
    public static final Sort SORT = Sort.by(Sort.Direction.ASC, "id");
    public static final String ITEM_NOT_FOUND = "Вещь с id - %d не найдена";

    public ItemServiceImpl(ItemStorage storage, UserStorage userStorage,
                           BookingStorage bookingStorage, CommentStorage commentStorage,
                           ItemRequestStorage itemRequestStorage) {
        this.storage = storage;
        this.userStorage = userStorage;
        this.bookingStorage = bookingStorage;
        this.commentStorage = commentStorage;
        this.itemRequestStorage = itemRequestStorage;
    }

    @Transactional
    @Override
    public ItemDto save(ItemDto itemDto, Long userId) {
        User owner = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND, userId)));
        Long requestId = itemDto.getRequestId();
        ItemRequest itemRequest = requestId == null ? null : itemRequestStorage.findById(requestId).orElse(null);
        Item item = ItemMapper.convertToItem(itemDto, owner, itemRequest);
        item = storage.save(item);
        if (itemRequest == null) {
            return ItemMapper.convertToItemDto(item);
        }
        return ItemMapper.convertToItemDtoWithRequestId(item);
    }

    @Transactional
    @Override
    public ItemDto update(String updatedFields, Long itemId, Long userId) {
        Item updatedItem = storage.findByIdAndOwnerId(itemId, userId)
                .orElseThrow(() -> new NotFoundException(String.format(ITEM_NOT_FOUND +
                        "у пользователя с id - %d", itemId, userId)));
        ObjectMapper mapper = new ObjectMapper();
        try {
            updatedItem = mapper.readerForUpdating(updatedItem).readValue(updatedFields);
        } catch (JsonProcessingException e) {
            throw new UpdateFailedException("Не удалось обновить данные");
        }
        storage.save(updatedItem);
        if (updatedItem.getRequest() != null) {
            return ItemMapper.convertToItemDtoWithRequestId(updatedItem);
        }
        return ItemMapper.convertToItemDto(updatedItem);
    }

    @Override
    public ItemDtoInfo get(Long id, Long userId) {
        Item item = storage.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(ITEM_NOT_FOUND, id)));
        Booking lastBooking = bookingStorage.findLastBooking(id, LocalDateTime.now(), userId).orElse(null);
        Booking nextBooking = bookingStorage.findNextBooking(id, LocalDateTime.now(), userId).orElse(null);
        List<Comment> comments = commentStorage.findAllByItemId(id);
        return ItemMapper.convertToItemDtoInfo(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemDtoInfo> getAllByUser(Long userId, Integer from, Integer size) {
        checkContainsUserInStorage(userId);
        Pageable pageRequest = PageRequest.of(from, size, SORT);
        List<Item> items = storage.findAllByOwnerId(userId, pageRequest);
        return convertItemListToDtoInfo(items, userId);
    }

    @Transactional
    @Override
    public void delete(Long id, Long userId) {
        storage.deleteItemByIdAndOwnerId(id, userId);
    }

    @Override
    public List<ItemDto> searchItem(String text, Long userId, Integer from, Integer size) {
        checkContainsUserInStorage(userId);
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        Pageable pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "item_id"));
        List<Item> searchedItems = storage.findAllByNameOrDescriptionLike(text, pageable);
        return convertItemListToDto(searchedItems);
    }

    @Transactional
    @Override
    public CommentDtoInfo saveComment(Long itemId, CommentDto commentDto, Long userId) {
        User author = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND, userId)));
        Item item = storage.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format(ITEM_NOT_FOUND, itemId)));
        List<Booking> itemBookings = bookingStorage.findByItemIdAndBookerIdAndStatusAndEndBefore(itemId, userId,
                BookingStatus.APPROVED, LocalDateTime.now());
        if (itemBookings.isEmpty()) {
            throw new UnavailableForUserException(String.format("Законченное бронирование вещи с id %d у пользователя " +
                    "с %d не найдено", itemId, userId));
        }
        Comment comment = CommentMapper.convertToComment(commentDto, item, author);
        comment.setCreated(LocalDateTime.now());
        commentStorage.save(comment);
        return CommentMapper.convertToCommentDtoInfo(comment, author);
    }

    private void checkContainsUserInStorage(Long userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException(String.format(USER_NOT_FOUND, userId));
        }
    }

    private List<ItemDtoInfo> convertItemListToDtoInfo(List<Item> items, Long userId) {
        return items.stream()
                .map(item -> ItemMapper.convertToItemDtoInfo(item,
                        bookingStorage.findLastBooking(item.getId(), LocalDateTime.now(), userId).orElse(null),
                        bookingStorage.findNextBooking(item.getId(), LocalDateTime.now(), userId).orElse(null),
                        commentStorage.findAllByItemId(item.getId())))
                .collect(Collectors.toList());
    }

    private List<ItemDto> convertItemListToDto(List<Item> items) {
        return items.stream()
                .map(ItemMapper::convertToItemDto)
                .collect(Collectors.toList());
    }
}
