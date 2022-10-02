package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {

    BookingDto save(BookingDto bookingDto, Long userId);

    BookingDtoInfo updateStatusOwner(Long id, Boolean approved, Long ownerId);

    BookingDtoInfo get(Long id, Long userId);

    List<BookingDtoInfo> getAllByBooker(State state, Long userId);

    List<BookingDtoInfo> getAllByOwner(State state, Long userId);

}
