package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item save(Item item);

    Item update(Item item);

    Item get(long id);

    List<Item> getAll(long userId);

    void delete(long id, long userId);

    List<Item> searchItem(String text);

    boolean containsInStorageByUser(long id, long userId);

    boolean containsInStorage(long id);

}
