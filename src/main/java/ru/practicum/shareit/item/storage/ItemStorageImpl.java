package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemStorageImpl implements ItemStorage {

    private final Map<Long, List<Item>> itemsByOwner;

    public ItemStorageImpl() {
        itemsByOwner = new HashMap<>();
    }

    @Override
    public Item save(Item item) {
        long ownerId = item.getOwnerId();
        if (!itemsByOwner.containsKey(ownerId)) {
            itemsByOwner.put(ownerId, new ArrayList<>());
        }
        itemsByOwner.get(ownerId).add(item);
        return item;
    }

    @Override
    public Item update(Item item) {
        itemsByOwner.get(item.getOwnerId()).remove(item);
        itemsByOwner.get(item.getOwnerId()).add(item);
        return item;
    }

    @Override
    public Item get(long id) {
        return itemsByOwner.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getId() == id)
                .findFirst()
                .get();
    }

    @Override
    public List<Item> getAll(long userId) {
        return itemsByOwner.get(userId);
    }

    @Override
    public void delete(long id, long userId) {
        Item itemToDelete = itemsByOwner.get(userId).stream()
                .filter(item -> item.getId() == id)
                .findFirst()
                .get();
        itemsByOwner.get(userId).remove(itemToDelete);
    }

    @Override
    public List<Item> searchItem(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemsByOwner.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())
                        && item.getAvailable()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean containsInStorage(long id) {
        if (itemsByOwner.isEmpty()) {
            return false;
        }
        return itemsByOwner.values().stream()
                .flatMap(Collection::stream)
                .anyMatch(item -> item.getId() == id);
    }

    @Override
    public boolean containsInStorageByUser(long id, long userId) {
        if (!itemsByOwner.containsKey(userId)) {
            return false;
        }
        return itemsByOwner.get(userId).stream()
                .anyMatch(item -> item.getId() == id);
    }
}
