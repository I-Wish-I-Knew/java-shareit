package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getAll();

    User get(long id);

    User save(User user);

    User update(User user);

    void delete(long id);

    boolean containsInStorage(long id);
}
