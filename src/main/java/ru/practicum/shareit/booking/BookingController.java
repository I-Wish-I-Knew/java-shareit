package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService service;

    @Autowired
    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    public BookingDto save(@RequestBody @Valid BookingDto bookingDto,
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
                                                       @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        return service.getAllByBooker(state, bookerId);
    }

    @GetMapping("/owner")
    public List<BookingDtoInfo> getAllByOwnerAndState(@RequestParam(required = false, defaultValue = "ALL") State state,
                                                      @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return service.getAllByOwner(state, ownerId);
    }
}
