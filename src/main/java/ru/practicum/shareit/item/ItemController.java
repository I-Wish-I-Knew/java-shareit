package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoInfo;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;

    @Autowired
    public ItemController(ItemService service) {
        this.service = service;
    }

    @GetMapping
    public List<ItemDtoInfo> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getAllByUser(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoInfo get(@PathVariable Long itemId,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.get(itemId, userId);
    }

    @PostMapping
    public ItemDto save(@RequestBody @Valid ItemDto itemDto,
                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.save(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoInfo saveComment(@PathVariable Long itemId,
                                      @RequestBody @Valid CommentDto commentDto,
                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.saveComment(itemId, commentDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId,
                          @RequestBody String updatedFields,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.update(updatedFields, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId,
                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        service.delete(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.searchItem(text, userId);
    }
}
