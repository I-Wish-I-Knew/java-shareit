package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemStorageTest {
    @Autowired
    private UserStorage userStorage;
    @Autowired
    private ItemStorage storage;
    @Autowired
    private ItemRequestStorage itemRequestStorage;
    private User owner;
    private User owner2;
    private ItemRequest itemRequest;
    private Item item1;
    private Item item2;
    private Item item3;
    private Item item4;

    @BeforeEach
    void setUp() {
        owner = userStorage.save(new User(
                1L,
                "user",
                "user@email.com"));
        owner2 = userStorage.save(new User(
                2L,
                "user2",
                "user2@email.com"));
        itemRequest = itemRequestStorage.save(new ItemRequest(
                1L,
                "description",
                owner2,
                LocalDateTime.now().withNano(0)));
        item1 = storage.save(new Item(
                1L,
                "name",
                "description1",
                owner,
                true,
                itemRequest));
        item2 = storage.save(new Item(
                2L,
                "name",
                "description2",
                owner,
                true,
                itemRequest));
        item3 = storage.save(new Item(
                3L,
                "name",
                "description3",
                owner2,
                true));
        item4 = storage.save(new Item(
                4L,
                "name",
                "description4",
                owner2,
                true));
    }

    @Test
    void findByIdWhereOwnerIdNot() {
        Optional<Item> emptyItem = storage.findByIdWhereOwnerIdNot(item1.getId(), owner.getId());
        Optional<Item> emptyItem2 = storage.findByIdWhereOwnerIdNot(item4.getId(), owner2.getId());

        assertThat(emptyItem).isEmpty();
        assertThat(emptyItem2).isEmpty();

        Optional<Item> item = storage.findByIdWhereOwnerIdNot(item1.getId(), owner2.getId());
        Optional<Item> item2 = storage.findByIdWhereOwnerIdNot(item3.getId(), owner.getId());

        assertThat(item).contains(item1);
        assertThat(item2).contains(item3);
    }

    @Test
    void findAllByOwnerId() {
        int from = 1;
        int size = 1;
        int page = from / size;
        List<Item> items1 = storage.findAllByOwnerId(owner.getId(), PageRequest.of(page, size, ItemServiceImpl.SORT));

        assertThat(items1).hasSize(1)
                .contains(item2);

        List<Item> items2 = storage.findAllByOwnerId(owner2.getId(), PageRequest.of(page, size, ItemServiceImpl.SORT));

        assertThat(items2).hasSize(1)
                .contains(item4);
    }

    @Test
    void findByIdAndOwnerId() {
        Optional<Item> emptyItem = storage.findByIdAndOwnerId(item1.getId(), owner2.getId());
        Optional<Item> emptyItem2 = storage.findByIdAndOwnerId(item4.getId(), owner.getId());

        assertThat(emptyItem).isEmpty();
        assertThat(emptyItem2).isEmpty();

        Optional<Item> item = storage.findByIdAndOwnerId(item1.getId(), owner.getId());
        Optional<Item> item2 = storage.findByIdAndOwnerId(item3.getId(), owner2.getId());

        assertThat(item).contains(item1);
        assertThat(item2).contains(item3);
    }

    @Test
    void deleteItemByIdAndOwnerId() {
        storage.deleteItemByIdAndOwnerId(item1.getId(), owner.getId());
        assertThat(storage.existsById(item1.getId())).isFalse();

        storage.deleteItemByIdAndOwnerId(item2.getId(), owner2.getId());
        assertThat(storage.existsById(item2.getId())).isTrue();
    }

    @Test
    void findAllByNameOrDescriptionLike() {
        int from = 2;
        int size = 2;
        int page = from / size;
        List<Item> items = storage.findAllByNameOrDescriptionLike("NaM",
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "item_id")));

        assertThat(items).hasSize(2)
                .contains(item3)
                .contains(item4);

        from = 1;
        size = 1;
        page = from / size;
        List<Item> items2 = storage.findAllByNameOrDescriptionLike("TiOn",
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "item_id")));
        assertThat(items2).hasSize(1).contains(item2);
    }

    @Test
    void findAllByRequestId() {
        List<Item> items = storage.findAllByRequestId(itemRequest.getId());

        assertThat(items).hasSize(2).contains(item1).contains(item2);
    }
}