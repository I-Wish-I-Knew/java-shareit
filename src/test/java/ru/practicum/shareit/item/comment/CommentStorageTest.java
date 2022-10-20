package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentStorageTest {
    @Autowired
    private CommentStorage storage;
    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private UserStorage userStorage;
    private Item item1;
    private Item item2;
    private Comment comment1;
    private Comment comment2;
    private Comment comment3;
    private Comment comment4;

    @BeforeEach
    void setUp() {
        User owner = userStorage.save(new User(
                1L,
                "user",
                "user@email.com"));
        item1 = itemStorage.save(new Item(
                1L, "name",
                "description",
                owner,
                true));
        item2 = itemStorage.save(new Item(
                2L,
                "name",
                "description",
                owner,
                true));
        comment1 = storage.save(new Comment(
                1L,
                "text",
                item1,
                owner,
                LocalDateTime.now().withNano(0)));
        comment2 = storage.save(new Comment(
                2L,
                "text",
                item1,
                owner,
                LocalDateTime.now().withNano(0)));
        comment3 = storage.save(new Comment(
                3L,
                "text",
                item2,
                owner,
                LocalDateTime.now().withNano(0)));
        comment4 = storage.save(new Comment(
                4L,
                "text",
                item2,
                owner,
                LocalDateTime.now().withNano(0)));
    }

    @Test
    void findAllById() {
        List<Comment> comments = storage.findAllByItemId(item1.getId());

        assertThat(comments).hasSize(2)
                .contains(comment1)
                .contains(comment2);

        List<Comment> comments2 = storage.findAllByItemId(item2.getId());

        assertThat(comments2).hasSize(2)
                .contains(comment3)
                .contains(comment4);
    }
}