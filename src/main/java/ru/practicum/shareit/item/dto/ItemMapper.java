package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    private ItemMapper() {
    }

    public static ItemDto convertToItemDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable());
    }

    public static Item convertToItem(ItemDto itemDto, User owner) {
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                owner,
                itemDto.getAvailable());
    }

    public static ItemDtoInfo convertToItemDtoInfo(Item item, Booking lastBooking,
                                                   Booking nextBooking, List<Comment> comments) {
        if (lastBooking == null && nextBooking == null) {
            return new ItemDtoInfo(item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    null,
                    null,
                    new ArrayList<>(comments.stream()
                            .map(ItemMapper::convertCommentForItemDtoInfo)
                            .collect(Collectors.toList())));
        } else if (lastBooking == null) {
            return new ItemDtoInfo(item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    null,
                    new ItemDtoInfo.Booking(nextBooking.getId(), nextBooking.getBooker().getId()),
                    new ArrayList<>(comments.stream()
                            .map(ItemMapper::convertCommentForItemDtoInfo)
                            .collect(Collectors.toList())));
        } else if (nextBooking == null) {
            return new ItemDtoInfo(item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    new ItemDtoInfo.Booking(lastBooking.getId(), lastBooking.getBooker().getId()),
                    null,
                    new ArrayList<>(comments.stream()
                            .map(ItemMapper::convertCommentForItemDtoInfo)
                            .collect(Collectors.toList())));
        }
        return new ItemDtoInfo(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                new ItemDtoInfo.Booking(lastBooking.getId(), lastBooking.getBooker().getId()),
                new ItemDtoInfo.Booking(nextBooking.getId(), nextBooking.getBooker().getId()),
                new ArrayList<>(comments.stream()
                        .map(ItemMapper::convertCommentForItemDtoInfo)
                        .collect(Collectors.toList())));
    }

    private static ItemDtoInfo.Comment convertCommentForItemDtoInfo(Comment comment) {
        return new ItemDtoInfo.Comment(comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }
}
