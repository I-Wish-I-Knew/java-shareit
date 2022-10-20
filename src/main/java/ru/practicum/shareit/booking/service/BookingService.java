package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.booking.dto.GetAllBookingsRequest;

import java.util.List;

public interface BookingService {

    BookingDtoInfo save(BookingDto bookingDto, Long userId);

    BookingDtoInfo updateStatusOwner(Long id, Boolean approved, Long ownerId);

    BookingDtoInfo get(Long id, Long userId);

    List<BookingDtoInfo> getAll(GetAllBookingsRequest request);

}
