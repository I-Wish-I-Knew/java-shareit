package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    private ItemService service;
    @Mock
    private ItemStorage storage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private BookingStorage bookingStorage;
    @Mock
    private CommentStorage commentStorage;
    @Mock
    private ItemRequestStorage itemRequestStorage;
    private Item item;
    private User owner;
    private User author;
    private Comment comment;
    private ItemRequest request;
    private Booking lastBooking;
    private Booking nextBooking;


    @BeforeEach
    void setUp() {
        service = new ItemServiceImpl(storage, userStorage, bookingStorage, commentStorage, itemRequestStorage);
        owner = new User(1L,
                "user1",
                "user1@email.com");
        author = new User(
                2L,
                "user2",
                "user2@email.com");
        User booker = new User(
                3L,
                "user3",
                "user3@email.com");
        request = new ItemRequest(
                1L,
                "description",
                author,
                LocalDateTime.now());
        item = new Item(
                1L,
                "item",
                "description",
                owner,
                true,
                request);
        comment = new Comment(
                1L,
                "comment",
                item,
                author,
                LocalDateTime.now());
        lastBooking = new Booking(
                1L,
                LocalDateTime.now().minusMonths(2).withNano(0),
                LocalDateTime.now().minusMonths(1).withNano(0),
                item,
                booker,
                BookingStatus.APPROVED);
        nextBooking = new Booking(
                2L,
                LocalDateTime.now().plusMonths(1).withNano(0),
                LocalDateTime.now().plusMonths(2).withNano(0),
                item,
                booker,
                BookingStatus.APPROVED);
    }

    @Test
    void saveWithRequest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRequestStorage.findById(anyLong())).thenReturn(Optional.of(request));
        when(storage.save(any())).thenReturn(item);

        final ItemDto itemDto = new ItemDto(2L,
                "name",
                "description",
                false,
                request.getId());
        ItemDto savedItemDto = service.save(itemDto, owner.getId());

        assertThat(savedItemDto).isNotNull()
                .isEqualTo(ItemMapper.convertToItemDtoWithRequestId(item));

        verify(userStorage, times(1))
                .findById(owner.getId());
        verify(itemRequestStorage, times(1))
                .findById(any());
        verify(storage, times(1))
                .save(ItemMapper.convertToItem(itemDto, owner, request));
    }

    @Test
    void saveWithoutRequest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(owner));
        when(storage.save(any())).thenReturn(item);

        final ItemDto itemDto = new ItemDto(
                2L,
                "name",
                "description",
                false);
        ItemDto savedItemDto = service.save(itemDto, owner.getId());

        item.setRequest(null);
        assertThat(savedItemDto).isNotNull()
                .isEqualTo(ItemMapper.convertToItemDto(item));

        verify(userStorage, times(1))
                .findById(owner.getId());
        verify(storage, times(1))
                .save(ItemMapper.convertToItem(itemDto, owner, null));
        item.setRequest(request);
    }

    @Test
    void update() {
        final Item updatedItem = new Item(item.getId(), "updatedName", item.getDescription(),
                item.getOwner(), item.getAvailable(), item.getRequest());

        when(storage.findByIdAndOwnerId(anyLong(), anyLong())).thenReturn(Optional.of(item));
        when(storage.save(any())).thenReturn(updatedItem);

        final ItemDto updatedItemResult = service.update("{\"name\": \"updatedName\"}", item.getId(),
                owner.getId());
        assertThat(updatedItemResult).isNotNull()
                .isEqualTo(ItemMapper.convertToItemDtoWithRequestId(updatedItem));

        verify(storage, times(1))
                .findByIdAndOwnerId(item.getId(), owner.getId());
        verify(storage, times(1))
                .save(updatedItem);
    }

    @Test
    void get() {
        when(storage.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingStorage.findLastBooking(anyLong(), any(), anyLong())).thenReturn(Optional.of(lastBooking));
        when(bookingStorage.findNextBooking(anyLong(), any(), anyLong())).thenReturn(Optional.of(nextBooking));
        when(commentStorage.findAllByItemId(anyLong())).thenReturn(Collections.singletonList(comment));

        final ItemDtoInfo itemDtoInfo = service.get(item.getId(), owner.getId());

        assertThat(itemDtoInfo).isNotNull().isEqualTo(ItemMapper.convertToItemDtoInfo(item, lastBooking, nextBooking,
                Collections.singletonList(comment)));

        verify(storage, times(1))
                .findById(item.getId());
        verify(bookingStorage, times(1))
                .findLastBooking(anyLong(), any(), anyLong());
        verify(bookingStorage, times(1))
                .findNextBooking(anyLong(), any(), anyLong());
        verify(commentStorage, times(1))
                .findAllByItemId(item.getId());
    }

    @Test
    void getAllByUser() {
        List<Item> items = new ArrayList<>(Collections.singletonList(item));

        when(userStorage.existsById(anyLong())).thenReturn(true);
        when(storage.findAllByOwnerId(anyLong(), any())).thenReturn(items);
        when(bookingStorage.findLastBooking(anyLong(), any(), anyLong())).thenReturn(Optional.of(lastBooking));
        when(bookingStorage.findNextBooking(anyLong(), any(), anyLong())).thenReturn(Optional.empty());

        List<ItemDtoInfo> itemDtoInfos = service.getAllByUser(owner.getId(), 1, 1);
        assertThat(itemDtoInfos).hasSize(1)
                .containsExactly(ItemMapper.convertToItemDtoInfo(items.get(0), lastBooking, null,
                        Collections.emptyList()));

        when(bookingStorage.findLastBooking(anyLong(), any(), anyLong())).thenReturn(Optional.empty());
        when(bookingStorage.findNextBooking(anyLong(), any(), anyLong())).thenReturn(Optional.of(nextBooking));

        List<ItemDtoInfo> itemDtoInfos2 = service.getAllByUser(owner.getId(), 1, 1);
        assertThat(itemDtoInfos2).hasSize(1)
                .containsExactly(ItemMapper.convertToItemDtoInfo(items.get(0), null, nextBooking,
                        Collections.emptyList()));

        verify(userStorage, times(2))
                .existsById(owner.getId());
        verify(bookingStorage, times(2))
                .findLastBooking(anyLong(), any(), anyLong());
        verify(bookingStorage, times(2))
                .findNextBooking(anyLong(), any(), anyLong());
        verify(commentStorage, times(2))
                .findAllByItemId(item.getId());
        verify(storage, times(2))
                .findAllByOwnerId(anyLong(), any());
    }

    @Test
    void delete() {
        service.delete(1L, 1L);

        verify(storage, times(1))
                .deleteItemByIdAndOwnerId(1L, 1L);
    }

    @Test
    void searchItem() {
        List<Item> items = new ArrayList<>(Collections.singletonList(item));

        when(userStorage.existsById(anyLong())).thenReturn(true);
        when(storage.findAllByNameOrDescriptionLike(anyString(), any())).thenReturn(items);

        List<ItemDto> itemsDto = service.searchItem("text", owner.getId(), 1, 1);
        assertThat(itemsDto).hasSize(1)
                .containsExactly(ItemMapper.convertToItemDto(items.get(0)));

        verify(userStorage, times(1))
                .existsById(owner.getId());
        verify(storage, times(1))
                .findAllByNameOrDescriptionLike(anyString(), any());
    }

    @Test
    void searchItemWithEmptyText() {
        when(userStorage.existsById(anyLong())).thenReturn(true);

        List<ItemDto> itemsDto = service.searchItem("", owner.getId(), 1, 1);
        assertThat(itemsDto).isEmpty();

        verify(userStorage, times(1))
                .existsById(owner.getId());
        verify(storage, times(0))
                .findAllByNameOrDescriptionLike(anyString(), any());
    }

    @Test
    void saveComment() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(author));
        when(storage.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingStorage.findByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(lastBooking));

        CommentDto commentDto = CommentMapper.convertToCommentDto(comment);
        CommentDtoInfo savedCommentDtoInfo = service.saveComment(item.getId(), commentDto, author.getId());

        assertThat(savedCommentDtoInfo).isNotNull()
                .hasFieldOrPropertyWithValue("text", comment.getText())
                .hasFieldOrPropertyWithValue("id", comment.getId())
                .hasFieldOrPropertyWithValue("authorName", comment.getAuthor().getName());

        verify(userStorage, times(1))
                .findById(author.getId());
        verify(storage, times(1))
                .findById(item.getId());
        verify(bookingStorage, times(1))
                .findByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any());
    }
}