package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.booking.dto.GetAllBookingsRequest;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService service;

    @Autowired
    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    public BookingDtoInfo save(@RequestBody @Valid BookingDto bookingDto,
                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.save(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoInfo updateStatusOwner(@PathVariable Long bookingId,
                                            @RequestParam Boolean approved,
                                            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return service.updateStatusOwner(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoInfo get(@PathVariable Long bookingId,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.get(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoInfo> getAllByBookerAndState(@RequestParam(required = false, defaultValue = "ALL") State state,
                                                       @RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                       @PositiveOrZero @RequestParam(value = "from",
                                                               defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(value = "size",
                                                               defaultValue = "10") Integer size) {

        return service.getAll(GetAllBookingsRequest.of(state, bookerId, false, from, size));
    }

    @GetMapping("/owner")
    public List<BookingDtoInfo> getAllByOwnerAndState(@RequestParam(required = false, defaultValue = "ALL") State state,
                                                      @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                      @PositiveOrZero @RequestParam(value = "from",
                                                              defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(value = "size",
                                                              defaultValue = "10") Integer size) {
        return service.getAll(GetAllBookingsRequest.of(state, ownerId, true, from, size));
    }
}
