package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInfo;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.service.UserServiceImpl.USER_NOT_FOUND;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestStorage storage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    public static final Sort SORT = Sort.by(Sort.Direction.DESC, "created");

    public ItemRequestServiceImpl(ItemRequestStorage storage, UserStorage userStorage,
                                  ItemStorage itemStorage) {
        this.storage = storage;
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
    }

    @Override
    public ItemRequestDto saveRequest(ItemRequestDto request, Long authorId) {
        User author = userStorage.findById(authorId)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND, authorId)));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(request, author);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest = storage.save(itemRequest);
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDtoInfo> getOwn(Long authorId) {
        checkUser(authorId);
        List<ItemRequest> authorRequests = storage.findAllByAuthorId(authorId, SORT);

        return authorRequests.stream()
                .map(request -> ItemRequestMapper.toItemRequestDtoInfo(request, getOffers(request.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDtoInfo> getAllPageable(Long userId, Integer from, Integer size) {
        checkUser(userId);
        Pageable pageRequest = PageRequest.of(from, size, SORT);
        return convertRequestList(storage.findAllByAuthorIdNot(userId, pageRequest));
    }

    @Override
    public ItemRequestDtoInfo get(Long id, Long userId) {
        checkUser(userId);
        ItemRequest request = storage.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос c id %d не найден", id)));
        return ItemRequestMapper.toItemRequestDtoInfo(request, getOffers(request.getId()));
    }

    private List<Item> getOffers(Long requestId) {
        return itemStorage.findAllByRequestId(requestId);
    }

    private void checkUser(Long userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException(String.format(USER_NOT_FOUND, userId));
        }
    }

    private List<ItemRequestDtoInfo> convertRequestList(List<ItemRequest> requests) {
        return requests.stream()
                .map(request -> ItemRequestMapper.toItemRequestDtoInfo(request, getOffers(request.getId())))
                .collect(Collectors.toList());
    }
}
