package ru.practicum.shareit.request;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInfo;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService service;

    public ItemRequestController(ItemRequestService service) {
        this.service = service;
    }

    @GetMapping
    public List<ItemRequestDtoInfo> getOwn(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(value = "from",
                                                   defaultValue = "0") Integer from,
                                           @RequestParam(value = "size",
                                                   defaultValue = "10") Integer size) {
        return service.getOwn(userId, from / size, size);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoInfo> getAllOtherUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(value = "from",
                                                           defaultValue = "0") Integer from,
                                                   @RequestParam(value = "size",
                                                           defaultValue = "10") Integer size) {
        return service.getAllOtherUser(userId, from / size, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoInfo get(@PathVariable Long requestId,
                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.get(requestId, userId);
    }

    @PostMapping
    public ItemRequestDto save(@RequestBody ItemRequestDto itemRequestDto,
                               @RequestHeader("X-Sharer-User-Id") Long authorId) {
        return service.save(itemRequestDto, authorId);
    }
}
