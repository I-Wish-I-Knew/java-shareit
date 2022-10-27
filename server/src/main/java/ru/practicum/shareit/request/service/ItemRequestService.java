package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInfo;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto save(ItemRequestDto request, Long authorId);

    List<ItemRequestDtoInfo> getOwn(Long userId, Integer page, Integer size);

    List<ItemRequestDtoInfo> getAllOtherUser(Long userId, Integer page, Integer size);

    ItemRequestDtoInfo get(Long id, Long userId);
}
