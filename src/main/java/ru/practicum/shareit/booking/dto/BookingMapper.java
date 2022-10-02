package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    private BookingMapper() {
    }

    public static BookingDto convertToBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getItem().getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus());
    }

    public static Booking convertToBooking(BookingDto bookingDto, Item item, User booker) {
        return new Booking(bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                booker,
                bookingDto.getStatus());
    }

    public static BookingDtoInfo convertToBookingDtoInfo(Booking booking) {
        return new BookingDtoInfo(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                new BookingDtoInfo.Item(booking.getItem().getId(), booking.getItem().getName()),
                new BookingDtoInfo.User(booking.getBooker().getId()));
    }
}
