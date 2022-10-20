package ru.practicum.shareit.request.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRequestStorageTest {
    @Autowired
    private UserStorage userStorage;
    @Autowired
    private ItemRequestStorage storage;
    private User user;
    private User user2;
    private ItemRequest itemRequest;
    private ItemRequest itemRequest2;
    private ItemRequest itemRequest3;
    private ItemRequest itemRequest4;
    private ItemRequest itemRequest5;
    private ItemRequest itemRequest6;

    @BeforeEach
    void setUp() {
        user = userStorage.save(new User(
                1L,
                "user1",
                "user1@email.com"));
        user2 = userStorage.save(new User(
                2L,
                "user2",
                "user2@email"));
        itemRequest = storage.save(new ItemRequest(
                1L,
                "description",
                user,
                LocalDateTime.now()));
        itemRequest2 = storage.save(new ItemRequest(
                2L,
                "description",
                user2,
                LocalDateTime.now().plusMinutes(1)));
        itemRequest3 = storage.save(new ItemRequest(
                3L,
                "description",
                user,
                LocalDateTime.now().plusMinutes(2)));
        itemRequest4 = storage.save(new ItemRequest(
                4L,
                "description",
                user2,
                LocalDateTime.now().plusMinutes(3)));
        itemRequest5 = storage.save(new ItemRequest(
                5L,
                "description",
                user,
                LocalDateTime.now().plusMinutes(4)));
        itemRequest6 = storage.save(new ItemRequest(
                6L,
                "description",
                user2,
                LocalDateTime.now().plusMinutes(5)));
    }

    @Test
    void findAllByAuthorId() {
        List<ItemRequest> itemRequests = storage.findAllByAuthorId(user.getId(), ItemRequestServiceImpl.SORT);
        assertThat(itemRequests).hasSize(3)
                .contains(itemRequest).contains(itemRequest3).contains(itemRequest5);

        List<ItemRequest> itemRequests2 = storage.findAllByAuthorId(user2.getId(), ItemRequestServiceImpl.SORT);
        assertThat(itemRequests2).hasSize(3)
                .contains(itemRequest2).contains(itemRequest4).contains(itemRequest6);
    }

    @Test
    void findAllByAuthorIdNot() {
        int from = 0;
        int size = 1;
        int page = from / size;
        List<ItemRequest> itemRequests = storage.findAllByAuthorIdNot(user.getId(),
                PageRequest.of(page, size, ItemRequestServiceImpl.SORT));
        assertThat(itemRequests).hasSize(1)
                .contains(itemRequest6);

        from = 2;
        size = 2;
        page = from / size;
        List<ItemRequest> itemRequests2 = storage.findAllByAuthorIdNot(user2.getId(),
                PageRequest.of(page, size, ItemRequestServiceImpl.SORT));
        assertThat(itemRequests2).hasSize(1)
                .contains(itemRequest);
    }
}