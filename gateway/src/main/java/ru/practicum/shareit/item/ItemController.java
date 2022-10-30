package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoInfo;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Controller
@RequestMapping(path = "/items")
@Slf4j
@Validated
public class ItemController {

    private final ItemClient client;

    public ItemController(ItemClient client) {
        this.client = client;
    }

    @GetMapping
    public ResponseEntity<List<ItemDtoInfo>> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @PositiveOrZero @RequestParam(value = "from",
                                                                  defaultValue = "0") Integer from,
                                                          @Positive @RequestParam(value = "size",
                                                                  defaultValue = "10") Integer size) {
        log.info("Get items by user {}, from={}, size={}", userId, from, size);
        return client.getAllByUser(userId, from / size, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDtoInfo> get(@PathVariable Long itemId,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get item {} by user {}", itemId, userId);
        return client.get(itemId, userId);
    }

    @PostMapping
    public ResponseEntity<ItemDto> save(@RequestBody @Valid ItemDto itemDto,
                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Save item {} by user {}", itemDto, userId);
        return client.save(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDtoInfo> saveComment(@PathVariable Long itemId,
                                                      @RequestBody @Valid CommentDto commentDto,
                                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Save comment {} for item {} by user {}", commentDto, itemId, userId);
        return client.saveComment(itemId, commentDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@PathVariable Long itemId,
                                          @RequestBody String updatedFields,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Update item {] fields {} by user {}", itemId, updatedFields, userId);
        return client.update(updatedFields, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId,
                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Delete item {} by user {}", itemId, userId);
        client.delete(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItem(@RequestParam String text,
                                                    @RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @PositiveOrZero @RequestParam(value = "from",
                                                            defaultValue = "0") Integer from,
                                                    @Positive @RequestParam(value = "size",
                                                            defaultValue = "10") Integer size) {
        log.info("Search for items with text {} by user {}, from={}, size={}", text, userId, from, size);
        return client.searchItem(text, userId, from / size, size);
    }
}
