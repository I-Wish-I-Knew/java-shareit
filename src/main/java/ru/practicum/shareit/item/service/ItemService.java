package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoInfo;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;

import java.util.List;

public interface ItemService {

    ItemDto save(ItemDto itemDto, Long userId);

    ItemDto update(String updatedFields, Long itemId, Long userId);

    ItemDtoInfo get(Long id, Long userId);

    List<ItemDtoInfo> getAllByUser(Long userId);

    void delete(Long id, Long userId);

    List<ItemDto> searchItem(String text, Long userId);

    CommentDtoInfo saveComment(Long itemId, CommentDto commentDto, Long userId);
}
