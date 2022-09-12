package ru.practicum.shareit.item.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserNotSpecifiedException;
import ru.practicum.shareit.item.converter.ItemConverter;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorage storage;
    private final UserStorage userStorage;
    private final ItemConverter converter;

    private long itemId;


    @Autowired
    public ItemServiceImpl(ItemStorage storage, UserStorage userStorage, ItemConverter converter) {
        this.storage = storage;
        this.userStorage = userStorage;
        this.converter = converter;
        this.itemId = 0;
    }

    @Override
    public ItemDto save(ItemDto itemDto, Long userId) {
        checkUserId(userId);
        checkContainsUserInStorage(userId);
        Item item = converter.convertToItem(itemDto);
        item.setId(++itemId);
        item.setOwnerId(userId);
        storage.save(item);
        return converter.convertToItemDto(item);
    }

    @Override
    public ItemDto update(String updatedFields, Long itemId, Long userId) {
        checkUserId(userId);
        checkContainsUserInStorage(userId);
        checkContainsItemInUserList(itemId, userId);
        Item updatedItem = storage.get(itemId);
        ObjectMapper mapper = new ObjectMapper();
        try {
            updatedItem = mapper.readerForUpdating(updatedItem).readValue(updatedFields);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        storage.update(updatedItem);
        return converter.convertToItemDto(updatedItem);
    }

    @Override
    public ItemDto get(Long id) {
        checkContainsItemInStorage(id);
        return converter.convertToItemDto(storage.get(id));
    }

    @Override
    public List<ItemDto> getAll(Long userId) {
        checkContainsUserInStorage(userId);
        List<Item> items = storage.getAll(userId);
        if (items == null) {
            return new ArrayList<>();
        }
        return items.stream()
                .map(converter::convertToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id, Long userId) {
        checkUserId(userId);
        checkContainsItemInUserList(id, userId);
        storage.delete(id, userId);
    }

    @Override
    public List<ItemDto> searchItem(String text, Long userId) {
        checkUserId(userId);
        checkContainsUserInStorage(userId);
        List<Item> searchedItems = storage.searchItem(text);
        if (searchedItems == null) {
            return new ArrayList<>();
        }
        return searchedItems.stream()
                .map(converter::convertToItemDto)
                .collect(Collectors.toList());
    }

    private void checkContainsItemInUserList(Long itemId, Long userId) {
        if (!storage.containsInStorageByUser(itemId, userId)) {
            throw new NotFoundException(String.format("Вещь с id - %d не найдена у пользователя с id - %d",
                    itemId, userId));
        }
    }

    private void checkContainsUserInStorage(Long userId) {
        if (!userStorage.containsInStorage(userId)) {
            throw new NotFoundException(String.format("Пользователь с id - %d не найден", userId));
        }
    }

    private void checkContainsItemInStorage(Long itemId) {
        if (!storage.containsInStorage(itemId)) {
            throw new NotFoundException(String.format("Вещь с id - %d не найдена", itemId));
        }
    }

    private void checkUserId(Long userId) {
        if (userId == null) {
            throw new UserNotSpecifiedException("Не указан идентификатор пользователя");
        }
    }
}
