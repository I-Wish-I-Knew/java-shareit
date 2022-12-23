package ru.practicum.shareit.request.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.request.service.ItemRequestServiceImpl.SORT;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    private ItemRequestService service;
    @Mock
    private ItemRequestStorage storage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private ItemStorage itemStorage;
    private User author;
    private ItemRequest request;
    private Item item;

    @BeforeEach
    void setUp() {
        service = new ItemRequestServiceImpl(storage, userStorage, itemStorage);
        author = new User(
                1L,
                "name",
                "user@email.com");
        request = new ItemRequest(
                1L,
                "description",
                author,
                LocalDateTime.now().withNano(0));
        item = new Item(
                1L,
                "item",
                "description",
                author,
                true,
                request);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(storage);
    }

    @Test
    void saveRequest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(author));
        when(storage.save(any())).thenReturn(request);

        final ItemRequestDto savedRequest = service.save(new ItemRequestDto(), author.getId());

        assertThat(savedRequest).isNotNull()
                .isEqualTo(ItemRequestMapper.toItemRequestDto(request));

        verify(userStorage, times(1))
                .findById(author.getId());
        verify(storage, times(1))
                .save(any());
    }

    @Test
    void saveRequestWithNotFound() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.empty());

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            service.save(itemRequestDto, 1L);
        });

        assertThat(thrown.getMessage()).isNotNull()
                .isEqualTo("Пользователь c id - " + author.getId() + " не найден");

        verify(userStorage, times(1))
                .findById(1L);
    }

    @Test
    void getOwn() {
        when(userStorage.existsById(anyLong())).thenReturn(true);
        when(itemStorage.findAllByRequestId(anyLong())).thenReturn(Collections.singletonList(item));
        when(storage.findAllByAuthorId(anyLong(), any())).thenReturn(Collections.singletonList(request));

        final List<ItemRequestDtoInfo> requests = service.getOwn(author.getId(),10, 10);
        Assertions.assertThat(requests).hasSize(1)
                .contains(ItemRequestMapper.toItemRequestDtoInfo(request, Collections.singletonList(item)));

        verify(userStorage, times(1))
                .existsById(author.getId());
        verify(itemStorage, atLeast(1))
                .findAllByRequestId(request.getId());
        verify(storage, times(1))
                .findAllByAuthorId(author.getId(), PageRequest.of(10,10,SORT));
    }

    @Test
    void getOwnWithNotFound() {
        when(userStorage.existsById(anyLong())).thenReturn(false);

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            service.getOwn(1L, 10, 10);
        });

        assertThat(thrown.getMessage()).isNotNull()
                .isEqualTo("Пользователь c id - " + author.getId() + " не найден");

        verify(userStorage, times(1))
                .existsById(1L);
    }

    @Test
    void getAllPageable() {
        when(userStorage.existsById(anyLong())).thenReturn(true);
        when(itemStorage.findAllByRequestId(anyLong())).thenReturn(Collections.singletonList(item));
        when(storage.findAllByAuthorIdNot(anyLong(), any())).thenReturn(Collections.singletonList(request));

        final List<ItemRequestDtoInfo> requests = service.getAllOtherUser(author.getId(), 10, 10);
        Assertions.assertThat(requests).hasSize(1)
                .contains(ItemRequestMapper.toItemRequestDtoInfo(request, Collections.singletonList(item)));

        verify(userStorage, times(1))
                .existsById(author.getId());
        verify(itemStorage, times(1))
                .findAllByRequestId(request.getId());
        verify(storage, atLeast(1))
                .findAllByAuthorIdNot(author.getId(), PageRequest.of(10, 10, SORT));
    }

    @Test
    void get() {
        when(userStorage.existsById(anyLong())).thenReturn(true);
        when(itemStorage.findAllByRequestId(anyLong())).thenReturn(Collections.singletonList(item));
        when(storage.findById(anyLong())).thenReturn(Optional.of(request));

        ItemRequestDtoInfo requestDtoInfo = service.get(request.getId(), author.getId());
        assertThat(requestDtoInfo).isNotNull()
                .isEqualTo(ItemRequestMapper.toItemRequestDtoInfo(request, Collections.singletonList(item)));

        verify(userStorage, times(1))
                .existsById(author.getId());
        verify(itemStorage, times(1))
                .findAllByRequestId(request.getId());
        verify(storage, times(1))
                .findById(request.getId());
    }

    @Test
    void getWithNotFound() {
        when(userStorage.existsById(anyLong())).thenReturn(true);
        when(storage.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            service.get(1L, 1L);
        });

        assertThat(thrown.getMessage()).isNotNull()
                .isEqualTo("Запрос c id " + request.getId() + " не найден");

        verify(userStorage, times(1))
                .existsById(1L);
        verify(storage, times(1))
                .findById(1L);
    }
}