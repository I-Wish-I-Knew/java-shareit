package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookingStorageTest {

    @Autowired
    private BookingStorage bookingStorage;
    @Autowired
    private UserStorage userStorage;
    @Autowired
    private ItemStorage itemStorage;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;

    @BeforeEach
    void setUp() {
        User user1 = userStorage.save(new User(
                1L,
                "user1",
                "user1@email.com"));
        User user2 = userStorage.save(new User(
                2L,
                "user2",
                "user2@email.com"));
        Item item1 = itemStorage.save(new Item(
                1L,
                "item1",
                "description",
                user2,
                true));
        Item item2 = itemStorage.save(new Item(
                2L,
                "item2",
                "description",
                user1,
                true));
        booking1 = bookingStorage.save(new Booking(
                1L,
                LocalDateTime.now().plusMonths(1),
                LocalDateTime.now().plusMonths(2),
                item1,
                user1,
                BookingStatus.WAITING));
        booking2 = bookingStorage.save(new Booking(
                2L,
                LocalDateTime.now().plusMonths(3),
                LocalDateTime.now().plusMonths(4),
                item1,
                user1,
                BookingStatus.APPROVED));
        booking3 = bookingStorage.save(new Booking(
                3L,
                LocalDateTime.now().plusMonths(1),
                LocalDateTime.now().plusMonths(2),
                item2,
                user2,
                BookingStatus.APPROVED));
    }

    @Test
    void findBookingByIdAndBookerId() {
        Optional<Booking> booking11 = bookingStorage.findBookingByIdAndBookerId(booking1.getId(),
                booking1.getBooker().getId());
        assertThat(booking11).isNotEmpty()
                .contains(booking1);

        Optional<Booking> booking21 = bookingStorage.findBookingByIdAndBookerId(booking2.getId(),
                booking2.getBooker().getId());
        assertThat(booking21).isNotEmpty()
                .contains(booking2);

        Optional<Booking> booking32 = bookingStorage.findBookingByIdAndBookerId(booking3.getId(),
                booking3.getBooker().getId());
        assertThat(booking32).isNotEmpty()
                .contains(booking3);
    }

    @Test
    void findBookingByIdAndItemOwnerId() {
        Optional<Booking> booking12 = bookingStorage.findBookingByIdAndItemOwnerId(booking1.getId(),
                booking1.getItem().getOwner().getId());
        assertThat(booking12).isNotEmpty()
                .contains(booking1);

        Optional<Booking> booking22 = bookingStorage.findBookingByIdAndItemOwnerId(booking2.getId(),
                booking2.getItem().getOwner().getId());
        assertThat(booking22).isNotEmpty()
                .contains(booking2);

        Optional<Booking> booking31 = bookingStorage.findBookingByIdAndItemOwnerId(booking3.getId(),
                booking3.getItem().getOwner().getId());
        assertThat(booking31).isNotEmpty()
                .contains(booking3);
    }

    @Test
    void findByItemIdAndBookerIdAndStatusAndEndBefore() {
        List<Booking> bookings1 = bookingStorage.findByItemIdAndBookerIdAndStatusAndEndBefore(booking1.getItem().getId(),
                booking1.getBooker().getId(), booking1.getStatus(), booking1.getEnd().plusMonths(1));
        assertThat(bookings1).isNotEmpty()
                .hasSize(1)
                .contains(booking1);

        List<Booking> bookings2 = bookingStorage.findByItemIdAndBookerIdAndStatusAndEndBefore(booking2.getItem().getId(),
                booking2.getBooker().getId(), booking2.getStatus(), booking2.getEnd().plusMonths(1));
        assertThat(bookings2).isNotEmpty()
                .hasSize(1)
                .contains(booking2);

        List<Booking> bookings3 = bookingStorage.findByItemIdAndBookerIdAndStatusAndEndBefore(booking3.getItem().getId(),
                booking3.getBooker().getId(), booking3.getStatus(), booking3.getEnd().plusMonths(1));
        assertThat(bookings3).isNotEmpty()
                .hasSize(1)
                .contains(booking3);
    }

    @Test
    void reservedForDates() {
        boolean reserved = bookingStorage.reservedForDates(booking1.getItem().getId(), booking1.getStart(),
                booking1.getEnd(), BookingStatus.APPROVED);
        assertThat(reserved).isFalse();

        reserved = bookingStorage.reservedForDates(booking2.getItem().getId(), booking2.getStart().minusMonths(1),
                booking2.getEnd().minusDays(20), BookingStatus.APPROVED);
        assertThat(reserved).isTrue();

        reserved = bookingStorage.reservedForDates(booking3.getItem().getId(), booking3.getStart().plusMonths(2),
                booking3.getEnd().plusMonths(3), BookingStatus.APPROVED);
        assertThat(reserved).isFalse();
    }

    @Test
    void findLastBooking() {
        Optional<Booking> lastBooking1 = bookingStorage.findLastBooking(booking1.getItem().getId(),
                booking1.getEnd().plusMonths(1), booking1.getItem().getOwner().getId());
        assertThat(lastBooking1).isNotEmpty()
                .contains(booking1);

        Optional<Booking> lastBooking2 = bookingStorage.findLastBooking(booking2.getItem().getId(),
                booking2.getEnd().plusMonths(1), booking2.getItem().getOwner().getId());
        assertThat(lastBooking2).isNotEmpty()
                .contains(booking2);

        Optional<Booking> lastBooking3 = bookingStorage.findLastBooking(booking3.getItem().getId(),
                booking3.getEnd().plusMonths(1), booking3.getItem().getOwner().getId());
        assertThat(lastBooking3).isNotEmpty()
                .contains(booking3);
    }

    @Test
    void findNextBooking() {
        Optional<Booking> nextBooking1 = bookingStorage.findNextBooking(booking1.getItem().getId(),
                booking1.getStart().minusDays(1), booking1.getItem().getOwner().getId());
        assertThat(nextBooking1).isNotEmpty()
                .contains(booking1);

        Optional<Booking> nextBooking2 = bookingStorage.findNextBooking(booking2.getItem().getId(),
                booking2.getStart().minusDays(1), booking2.getItem().getOwner().getId());
        assertThat(nextBooking2).isNotEmpty()
                .contains(booking2);

        Optional<Booking> nextBooking3 = bookingStorage.findNextBooking(booking3.getItem().getId(),
                booking3.getStart().minusDays(1), booking3.getItem().getOwner().getId());
        assertThat(nextBooking3).isNotEmpty()
                .contains(booking3);
    }
}