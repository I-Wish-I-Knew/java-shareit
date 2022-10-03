package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableItemException;
import ru.practicum.shareit.exception.UnchangeableStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.service.ItemServiceImpl.ITEM_NOT_FOUND;
import static ru.practicum.shareit.user.service.UserServiceImpl.USER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingStorage storage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    @PersistenceContext
    private EntityManager entityManager;
    public static final String BOOKING_NOT_FOUND = "Бронирование с id -" +
            " %d не найдено";

    public BookingServiceImpl(BookingStorage storage, UserStorage userStorage,
                              ItemStorage itemStorage) {
        this.storage = storage;
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
    }

    @Override
    @Transactional
    public BookingDto save(BookingDto bookingDto, Long userId) {
        User booker = userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format(USER_NOT_FOUND, userId)));
        Item item = itemStorage.findByIdWhereOwnerIdNot(bookingDto.getItemId(), userId).orElseThrow(() ->
                new NotFoundException(String.format(ITEM_NOT_FOUND, bookingDto.getItemId())));
        if (storage.reservedForDates(bookingDto.getId(), bookingDto.getEnd(),
                bookingDto.getStart(), BookingStatus.APPROVED) || Boolean.FALSE.equals(item.getAvailable())) {
            throw new UnavailableItemException(String.format("Вещь с id - %d не доступна " +
                    "для бронирования", item.getId()));
        }
        bookingDto.setStatus(BookingStatus.WAITING);
        Booking booking = BookingMapper.convertToBooking(bookingDto, item, booker);
        return BookingMapper.convertToBookingDto(storage.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoInfo updateStatusOwner(Long id, Boolean approved, Long ownerId) {
        Booking booking = storage.findBookingByIdAndItemOwnerId(id, ownerId).orElseThrow(() ->
                new NotFoundException(String.format(BOOKING_NOT_FOUND, id)));
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new UnchangeableStatusException(String.format("Статус бронирования с id %d - %s " +
                    "больше нельзя изменить", id, booking.getStatus().name()));
        }
        if (Boolean.TRUE.equals(approved)) {
            booking.setStatus(BookingStatus.APPROVED);
        } else if (Boolean.FALSE.equals(approved)) {
            booking.setStatus(BookingStatus.REJECTED);
        }
        storage.save(booking);
        return BookingMapper.convertToBookingDtoInfo(booking);
    }

    @Override
    public BookingDtoInfo get(Long id, Long userId) {
        Booking booking = storage.findBookingByIdAndItemOwnerId(id, userId)
                .or(() -> storage.findBookingByIdAndBookerId(id, userId)).orElseThrow(() ->
                        new NotFoundException(String.format(BOOKING_NOT_FOUND, id)));
        return BookingMapper.convertToBookingDtoInfo(booking);
    }

    @Override
    public List<BookingDtoInfo> getAll(State state, Long userId, boolean isOwner) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Booking> cq = cb.createQuery(Booking.class);
        Root<Booking> booking = cq.from(Booking.class);
        List<Predicate> predicates = new ArrayList<>();
        if (isOwner) {
            predicates.add(cb.equal(booking.get("item").get("owner").get("id"), userId));
        } else {
            predicates.add(cb.equal(booking.get("booker").get("id"), userId));
        }
        switch (state) {
            case CURRENT:
                predicates.add(cb.lessThanOrEqualTo(booking.get("start"), LocalDateTime.now()));
                predicates.add(cb.greaterThan(booking.get("end"), LocalDateTime.now()));
                break;
            case FUTURE:
                predicates.add(cb.greaterThan(booking.get("start"), LocalDateTime.now()));
                break;
            case PAST:
                predicates.add(cb.lessThan(booking.get("end"), LocalDateTime.now()));
                break;
            case REJECTED:
                predicates.add(cb.equal(booking.get("status"), BookingStatus.REJECTED));
                break;
            case WAITING:
                predicates.add(cb.equal(booking.get("status"), BookingStatus.WAITING));
                break;
            case ALL:
                break;
        }
        cq.select(booking).where(predicates.toArray(new Predicate[]{})).orderBy(cb.desc(booking.get("start")));
        List<Booking> bookings = entityManager.createQuery(cq).getResultList();
        if (bookings.isEmpty()) {
            throw new NotFoundException(String.format("Бронирования для пользователя с id %d не найдены", userId));
        }
        return bookings.stream()
                .map(BookingMapper::convertToBookingDtoInfo)
                .collect(Collectors.toList());
    }
}
