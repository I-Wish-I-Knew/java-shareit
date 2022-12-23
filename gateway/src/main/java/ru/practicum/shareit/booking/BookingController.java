package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.booking.dto.GetAllBookingsRequest;
import ru.practicum.shareit.booking.model.State;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Controller
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingController {

    private final BookingClient client;

    public BookingController(BookingClient client) {
        this.client = client;
    }

    @PostMapping
    public ResponseEntity<BookingDtoInfo> save(@RequestBody @Valid BookingDto bookingDto,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Save booking {}, userId={}", bookingDto, userId);
        return client.save(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDtoInfo> updateStatusOwner(@PathVariable Long bookingId,
                                                            @RequestParam Boolean approved,
                                                            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Update status to approved={} for booking {} by user {}", approved, bookingId, ownerId);
        return client.updateStatusOwner(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDtoInfo> get(@PathVariable Long bookingId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get booking {} by user {}", bookingId, userId);
        return client.get(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<List<BookingDtoInfo>> getAllByBookerAndState(@RequestParam(required = false,
                                                                                     defaultValue = "ALL") State state,
                                                                       @RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                                       @PositiveOrZero @RequestParam(value = "from",
                                                                               defaultValue = "0") Integer from,
                                                                       @Positive @RequestParam(value = "size",
                                                                               defaultValue = "10") Integer size) {
        log.info("Get bookings for booker {} with state {}, from={}, size={}", bookerId, state, from, size);
        return client.getAll(GetAllBookingsRequest.of(state, bookerId, false, from, size));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDtoInfo>> getAllByOwnerAndState(@RequestParam(required = false,
                                                                                    defaultValue = "ALL") State state,
                                                                      @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                                      @PositiveOrZero @RequestParam(value = "from",
                                                                              defaultValue = "0") Integer from,
                                                                      @Positive @RequestParam(value = "size",
                                                                              defaultValue = "10") Integer size) {
        log.info("Get bookings for owner {} with state {}, from={}, size={}", ownerId, state, from, size);
        return client.getAll(GetAllBookingsRequest.of(state, ownerId, true, from, size));
    }
}
