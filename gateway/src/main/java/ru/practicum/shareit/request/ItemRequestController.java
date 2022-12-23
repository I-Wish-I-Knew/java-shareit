package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInfo;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Controller
@RequestMapping(path = "/requests")
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient client;

    public ItemRequestController(ItemRequestClient client) {
        this.client = client;
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDtoInfo>> getOwn(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @PositiveOrZero @RequestParam(value = "from",
                                                                   defaultValue = "0") Integer from,
                                                           @Positive @RequestParam(value = "size",
                                                                   defaultValue = "10") Integer size) {
        log.info("Get itemRequests by author {}, from={}, size={}", userId, from, size);
        return client.getOwn(userId, from / size, size);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDtoInfo>> getAllOtherUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                    @PositiveOrZero @RequestParam(value = "from",
                                                                           defaultValue = "0") Integer from,
                                                                    @Positive @RequestParam(value = "size",
                                                                           defaultValue = "10") Integer size) {
        log.info("Get itemRequests by user {}, from={}, size={}", userId, from, size);
        return client.getAllOtherUser(userId, from / size, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDtoInfo> get(@PathVariable Long requestId,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get itemRequest {} by user {}", requestId, userId);
        return client.get(requestId, userId);
    }

    @PostMapping
    public ResponseEntity<ItemRequestDto> save(@RequestBody @Valid ItemRequestDto itemRequestDto,
                                               @RequestHeader("X-Sharer-User-Id") Long authorId) {
        log.info("Save itemRequest {} by user {}", itemRequestDto, authorId);
        return client.save(itemRequestDto, authorId);
    }
}
