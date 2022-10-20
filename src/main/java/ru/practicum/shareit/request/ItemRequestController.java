package ru.practicum.shareit.request;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInfo;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService service;

    public ItemRequestController(ItemRequestService service) {
        this.service = service;
    }

    @GetMapping
    public List<ItemRequestDtoInfo> getOwn(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getOwn(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoInfo> getAllPageable(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @PositiveOrZero @RequestParam(value = "from",
                                                           defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(value = "size",
                                                           defaultValue = "10") Integer size) {
        return service.getAllPageable(userId, from / size, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoInfo get(@PathVariable Long requestId,
                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.get(requestId, userId);
    }

    @PostMapping
    public ItemRequestDto save(@RequestBody @Valid ItemRequestDto itemRequestDto,
                               @RequestHeader("X-Sharer-User-Id") Long authorId) {
        return service.saveRequest(itemRequestDto, authorId);
    }
}
