package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoInfo;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;

    @Autowired
    public ItemController(ItemService service) {
        this.service = service;
    }

    @GetMapping
    public List<ItemDtoInfo> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(value = "from",
                                                  defaultValue = "0") Integer from,
                                          @RequestParam(value = "size",
                                                  defaultValue = "10") Integer size) {
        return service.getAllByUser(userId, from / size, size);
    }

    @GetMapping("/{itemId}")
    public ItemDtoInfo get(@PathVariable Long itemId,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.get(itemId, userId);
    }

    @PostMapping
    public ItemDto save(@RequestBody ItemDto itemDto,
                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.save(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoInfo saveComment(@PathVariable Long itemId,
                                      @RequestBody CommentDto commentDto,
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
                                    @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestParam(value = "from",
                                            defaultValue = "0") Integer from,
                                    @RequestParam(value = "size",
                                            defaultValue = "10") Integer size) {
        return service.searchItem(text, userId, from / size, size);
    }
}
