package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableItemException;
import ru.practicum.shareit.exception.UnchangeableStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.service.BookingServiceImpl.BOOKING_NOT_FOUND;
import static ru.practicum.shareit.item.service.ItemServiceImpl.ITEM_NOT_FOUND;
import static ru.practicum.shareit.user.service.UserServiceImpl.USER_NOT_FOUND;

@Transactional
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    private BookingService service;
    @Mock
    private BookingStorage storage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private ItemStorage itemStorage;
    private Booking booking;
    private Item item;
    private User owner;

    @BeforeEach
    void setUp() {
        service = new BookingServiceImpl(storage, userStorage, itemStorage);
        owner = new User(
                1L,
                "user",
                "user@email.com");
        item = new Item(
                1L,
                "item",
                "description",
                owner,
                true);
        booking = new Booking(
                1L,
                LocalDateTime.now().plusMonths(1).withNano(0),
                LocalDateTime.now().plusMonths(2).withNano(0),
                item,
                owner,
                BookingStatus.WAITING);
    }

    @Test
    void save() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemStorage.findByIdWhereOwnerIdNot(anyLong(), anyLong())).thenReturn(Optional.of(item));
        when(storage.save(any())).thenReturn(booking);
        when(storage.reservedForDates(anyLong(), any(), any(), any())).thenReturn(false);

        final Booking bookingForSave = new Booking(
                2L,
                LocalDateTime.now().plusMonths(5),
                LocalDateTime.now().plusMonths(6),
                item,
                owner,
                BookingStatus.REJECTED);
        final BookingDtoInfo savedBookingDto = service.save(BookingMapper.convertToBookingDto(bookingForSave),
                owner.getId());

        assertThat(savedBookingDto).isNotNull()
                .isEqualTo(BookingMapper.convertToBookingDtoInfo(booking));

        bookingForSave.setStatus(BookingStatus.WAITING);

        verify(userStorage, times(1))
                .findById(owner.getId());
        verify(itemStorage, times(1))
                .findByIdWhereOwnerIdNot(any(), any());
        verify(storage, times(1))
                .reservedForDates(booking.getItem().getId(), bookingForSave.getStart(),
                        bookingForSave.getEnd(), BookingStatus.APPROVED);
        verify(storage, times(1))
                .save(bookingForSave);
    }

    @Test
    void saveWithUserNotFound() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.empty());

        BookingDto bookingDto = BookingMapper.convertToBookingDto(booking);
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            service.save(bookingDto, 1L);
        });

        assertThat(thrown.getMessage()).isNotNull()
                .isEqualTo(String.format(USER_NOT_FOUND, owner.getId()));

        verify(userStorage, times(1))
                .findById(1L);
    }

    @Test
    void saveWithItemNotFound() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemStorage.findByIdWhereOwnerIdNot(anyLong(), anyLong())).thenReturn(Optional.empty());

        BookingDto bookingDto = BookingMapper.convertToBookingDto(booking);
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            service.save(bookingDto, 1L);
        });

        assertThat(thrown.getMessage()).isNotNull()
                .isEqualTo(String.format(ITEM_NOT_FOUND, item.getId()));

        verify(userStorage, times(1))
                .findById(anyLong());
        verify(itemStorage, times(1))
                .findByIdWhereOwnerIdNot(anyLong(), anyLong());
    }

    @Test
    void saveWithItemUnavailableItem() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemStorage.findByIdWhereOwnerIdNot(anyLong(), anyLong())).thenReturn(Optional.of(item));
        when(storage.reservedForDates(anyLong(), any(), any(), any())).thenReturn(true);

        BookingDto bookingDto = BookingMapper.convertToBookingDto(booking);
        UnavailableItemException thrown = assertThrows(UnavailableItemException.class, () -> {
            service.save(bookingDto, 1L);
        });

        assertThat(thrown.getMessage()).isNotNull()
                .isEqualTo(String.format("Вещь с id - %d не доступна " +
                        "для бронирования", item.getId()));

        verify(userStorage, times(1))
                .findById(anyLong());
        verify(itemStorage, times(1))
                .findByIdWhereOwnerIdNot(anyLong(), anyLong());
        verify(storage, times(1))
                .reservedForDates(any(), any(), any(), any());
    }

    @Test
    void updateWithBookingNotFound() {
        when(storage.findBookingByIdAndItemOwnerId(anyLong(), anyLong())).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            service.updateStatusOwner(1L, false, 1L);
        });

        assertThat(thrown.getMessage()).isNotNull()
                .isEqualTo(String.format(BOOKING_NOT_FOUND, booking.getId()));

        verify(storage, times(1))
                .findBookingByIdAndItemOwnerId(1L, 1L);
    }

    @Test
    void updateWithUnchangeableStatus() {
        booking.setStatus(BookingStatus.REJECTED);
        when(storage.findBookingByIdAndItemOwnerId(anyLong(), anyLong())).thenReturn(Optional.of(booking));

        UnchangeableStatusException thrown = assertThrows(UnchangeableStatusException.class, () -> {
            service.updateStatusOwner(1L, false, 1L);
        });

        assertThat(thrown.getMessage()).isNotNull()
                .isEqualTo(String.format("Статус бронирования с id %d - %s " +
                        "больше нельзя изменить", booking.getId(), booking.getStatus().name()));

        verify(storage, times(1))
                .findBookingByIdAndItemOwnerId(1L, 1L);
    }


    @Test
    void updateStatusOwnerApproved() {
        when(storage.findBookingByIdAndItemOwnerId(anyLong(), anyLong())).thenReturn(Optional.of(booking));
        when(storage.save(any())).thenReturn(booking);

        final BookingDtoInfo bookingDtoInfo = service.updateStatusOwner(1L, true, 1L);
        final Booking approvedBooking = new Booking(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                BookingStatus.APPROVED);

        assertThat(bookingDtoInfo).isNotNull()
                .isEqualTo(BookingMapper.convertToBookingDtoInfo(booking));

        verify(storage, times(1))
                .findBookingByIdAndItemOwnerId(1L, 1L);
        verify(storage, times(1))
                .save(approvedBooking);
    }

    @Test
    void updateStatusOwnerRejected() {
        when(storage.findBookingByIdAndItemOwnerId(anyLong(), anyLong())).thenReturn(Optional.of(booking));
        when(storage.save(any())).thenReturn(booking);

        final BookingDtoInfo bookingDtoInfo = service.updateStatusOwner(1L, false, 1L);
        final Booking rejectedBooking = new Booking(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                BookingStatus.REJECTED);

        assertThat(bookingDtoInfo).isNotNull()
                .isEqualTo(BookingMapper.convertToBookingDtoInfo(booking));

        verify(storage, times(1))
                .findBookingByIdAndItemOwnerId(1L, 1L);
        verify(storage, times(1))
                .save(rejectedBooking);
    }

    @Test
    void get() {
        when(storage.findBookingByIdAndItemOwnerId(anyLong(), anyLong())).thenReturn(Optional.of(booking));

        final BookingDtoInfo bookingDtoInfo = service.get(booking.getId(), owner.getId());

        assertThat(bookingDtoInfo).isNotNull()
                .isEqualTo(BookingMapper.convertToBookingDtoInfo(booking));

        verify(storage, times(1))
                .findBookingByIdAndItemOwnerId(booking.getId(), owner.getId());
    }

    @Test
    void getWithNotFound() {
        when(storage.findBookingByIdAndItemOwnerId(anyLong(), anyLong())).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            service.get(1L, 1L);
        });

        assertThat(thrown.getMessage()).isNotNull()
                .isEqualTo(String.format(BOOKING_NOT_FOUND, booking.getId()));

        verify(storage, times(1))
                .findBookingByIdAndItemOwnerId(1L, 1L);
    }
}