package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto save(ItemDto itemDto, Long userId);

    ItemDto update(String updatedFields, Long itemId, Long userId);

    ItemDto get(Long id);

    List<ItemDto> getAll(Long userId);

    void delete(Long id, Long userId);

    List<ItemDto> searchItem(String text, Long userId);
}
