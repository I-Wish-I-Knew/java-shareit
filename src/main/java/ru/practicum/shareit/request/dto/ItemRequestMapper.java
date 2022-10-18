package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    private ItemRequestMapper() {

    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(),
                                  itemRequest.getDescription(),
                                  itemRequest.getCreated());
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto,
                                            User author) {
        return new ItemRequest(itemRequestDto.getId(),
                               itemRequestDto.getDescription(),
                               author,
                               itemRequestDto.getCreated());
    }

    public static ItemRequestDtoInfo toItemRequestDtoInfo(ItemRequest itemRequest,
                                                          List<Item> offers) {
        return new ItemRequestDtoInfo(itemRequest.getId(),
                                      itemRequest.getDescription(),
                                      itemRequest.getCreated(),
                                      new ArrayList<>(offers.stream()
                                              .map(ItemRequestMapper::convertItemForRequest)
                                              .collect(Collectors.toList())));
    }

    private static ItemRequestDtoInfo.Item convertItemForRequest(Item item) {
        return new ItemRequestDtoInfo.Item(item.getId(),
                                           item.getName(),
                                           item.getDescription(),
                                           item.getAvailable(),
                                           item.getRequest().getId());
    }
}
