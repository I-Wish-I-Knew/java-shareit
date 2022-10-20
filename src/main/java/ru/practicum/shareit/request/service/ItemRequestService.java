package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInfo;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto saveRequest(ItemRequestDto request, Long authorId);

    List<ItemRequestDtoInfo> getOwn(Long userId);

    List<ItemRequestDtoInfo> getAllPageable(Long userId, Integer from, Integer size);

    ItemRequestDtoInfo get(Long id, Long userId);
}
