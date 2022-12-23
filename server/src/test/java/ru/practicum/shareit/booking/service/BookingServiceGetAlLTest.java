package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.GetAllBookingsRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceGetAlLTest {

    private final EntityManager em;
    private final BookingService service;
    @Autowired
    private final BookingStorage storage;
    @Autowired
    private final UserStorage userStorage;
    @Autowired
    private final ItemStorage itemStorage;
    private User user1;
    private User user2;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;
    private Booking booking4;
    private Booking booking5;
    private Booking booking6;

    @BeforeEach
    void setUp() {
        user1 = userStorage.save(new User(
                1L,
                "user",
                "user@email.com"));
        user2 = userStorage.save(new User(
                2L,
                "user2",
                "user2@email.com"));

        Item item1 = itemStorage.save(new Item(
                1L, "item",
                "description",
                user1,
                true));
        Item item2 = itemStorage.save(new Item(
                2L, "item",
                "description",
                user2,
                true));

        booking1 = storage.save(new Booking(
                1L,
                LocalDateTime.now().minusMonths(2).withNano(0),
                LocalDateTime.now().minusMonths(1).withNano(0),
                item1,
                user2,
                BookingStatus.WAITING));

        booking2 = storage.save(new Booking(
                2L,
                LocalDateTime.now().plusMonths(1).withNano(0),
                LocalDateTime.now().plusMonths(2).withNano(0),
                item1,
                user2,
                BookingStatus.WAITING));

        booking3 = storage.save(new Booking(
                3L,
                LocalDateTime.now().minusMonths(1).withNano(0),
                LocalDateTime.now().plusMonths(1).withNano(0),
                item2,
                user1,
                BookingStatus.REJECTED));

        booking4 = storage.save(new Booking(
                4L,
                LocalDateTime.now().plusMonths(3).withNano(0),
                LocalDateTime.now().plusMonths(4).withNano(0),
                item2,
                user1,
                BookingStatus.WAITING));

        booking5 = storage.save(new Booking(
                5L,
                LocalDateTime.now().minusMonths(2).withNano(0),
                LocalDateTime.now().plusDays(1).withNano(0),
                item1,
                user2,
                BookingStatus.WAITING));

        booking6 = storage.save(new Booking(
                6L,
                LocalDateTime.now().minusMonths(1).withNano(0),
                LocalDateTime.now().minusDays(1).withNano(0),
                item2,
                user1,
                BookingStatus.WAITING));
    }

    @Test
    void getAllCurrentOwnerTest() {
        GetAllBookingsRequest request = GetAllBookingsRequest.of(State.CURRENT, user1.getId(),
                true, 0, 1);
        List<BookingDtoInfo> currentOwnerBookingsDtoInfo = service.getAll(request);

        TypedQuery<Booking> query = em.createQuery("select b from Booking b " +
                "where b.item.owner.id = :ownerId and b.start < :now and b.end > :now " +
                "order by b.start desc", Booking.class);
        List<Booking> currentOwnerBookings = query.setParameter("ownerId", request.getUserId())
                .setParameter("now", LocalDateTime.now())
                .setFirstResult(request.getFrom())
                .setMaxResults(request.getSize())
                .getResultList();

        Assertions.assertThat(currentOwnerBookingsDtoInfo)
                .hasSameSizeAs(currentOwnerBookings)
                .hasSize(1)
                .contains(BookingMapper.convertToBookingDtoInfo(booking5));
        Assertions.assertThat(currentOwnerBookings).contains(booking5);
    }

    @Test
    void getAllCurrentBookerTest() {
        GetAllBookingsRequest request = GetAllBookingsRequest.of(State.CURRENT, user2.getId(),
                false, 0, 1);
        List<BookingDtoInfo> currentBookerBookingsDtoInfo = service.getAll(request);

        TypedQuery<Booking> query = em.createQuery("select b from Booking b " +
                "where b.booker.id = :bookerId and b.start < :now and b.end > :now " +
                "order by b.start desc", Booking.class);
        List<Booking> currentBookerBookings = query.setParameter("bookerId", request.getUserId())
                .setParameter("now", LocalDateTime.now())
                .setFirstResult(request.getFrom())
                .setMaxResults(request.getSize())
                .getResultList();

        Assertions.assertThat(currentBookerBookingsDtoInfo)
                .hasSameSizeAs(currentBookerBookings)
                .hasSize(1)
                .contains(BookingMapper.convertToBookingDtoInfo(booking5));
        Assertions.assertThat(currentBookerBookings).contains(booking5);
    }

    @Test
    void getAllFutureOwnerTest() {
        GetAllBookingsRequest request = GetAllBookingsRequest.of(State.FUTURE, user1.getId(),
                true, 0, 1);
        List<BookingDtoInfo> futureOwnerBookingsDtoInfo = service.getAll(request);

        TypedQuery<Booking> query = em.createQuery("select b from Booking b " +
                "where b.item.owner.id = :ownerId and b.start > :now " +
                "order by b.start desc", Booking.class);
        List<Booking> futureOwnerBookings = query.setParameter("ownerId", request.getUserId())
                .setParameter("now", LocalDateTime.now())
                .setFirstResult(request.getFrom())
                .setMaxResults(request.getSize())
                .getResultList();

        Assertions.assertThat(futureOwnerBookingsDtoInfo)
                .hasSameSizeAs(futureOwnerBookings)
                .hasSize(1)
                .contains(BookingMapper.convertToBookingDtoInfo(booking2));
        Assertions.assertThat(futureOwnerBookings).contains(booking2);
    }

    @Test
    void getAllFutureBookerTest() {
        GetAllBookingsRequest request = GetAllBookingsRequest.of(State.FUTURE, user2.getId(),
                false, 0, 1);
        List<BookingDtoInfo> futureBookerBookingsDtoInfo = service.getAll(request);

        TypedQuery<Booking> query = em.createQuery("select b from Booking b " +
                "where b.booker.id = :bookerId and b.start > :now " +
                "order by b.start desc", Booking.class);
        List<Booking> futureBookerBookings = query.setParameter("bookerId", request.getUserId())
                .setParameter("now", LocalDateTime.now())
                .setFirstResult(request.getFrom())
                .setMaxResults(request.getSize())
                .getResultList();

        Assertions.assertThat(futureBookerBookingsDtoInfo)
                .hasSameSizeAs(futureBookerBookings)
                .hasSize(1)
                .contains(BookingMapper.convertToBookingDtoInfo(booking2));
        Assertions.assertThat(futureBookerBookings).contains(booking2);
    }

    @Test
    void getAllPastOwnerTest() {
        GetAllBookingsRequest request = GetAllBookingsRequest.of(State.PAST, user1.getId(),
                true, 0, 1);
        List<BookingDtoInfo> pastOwnerBookingsDtoInfo = service.getAll(request);

        TypedQuery<Booking> query = em.createQuery("select b from Booking b " +
                "where b.item.owner.id = :ownerId and b.end < :now " +
                "order by b.start desc", Booking.class);
        List<Booking> pastOwnerBookings = query.setParameter("ownerId", request.getUserId())
                .setParameter("now", LocalDateTime.now())
                .setFirstResult(request.getFrom())
                .setMaxResults(request.getSize())
                .getResultList();

        Assertions.assertThat(pastOwnerBookingsDtoInfo)
                .hasSameSizeAs(pastOwnerBookings)
                .hasSize(1)
                .contains(BookingMapper.convertToBookingDtoInfo(booking1));
        Assertions.assertThat(pastOwnerBookings).contains(booking1);
    }

    @Test
    void getAllPastBookerTest() {
        GetAllBookingsRequest request = GetAllBookingsRequest.of(State.PAST, user1.getId(),
                false, 0, 1);
        List<BookingDtoInfo> pastBookerBookingsDtoInfo = service.getAll(request);

        TypedQuery<Booking> query = em.createQuery("select b from Booking b " +
                "where b.booker.id = :bookerId and b.end < :now " +
                "order by b.start desc", Booking.class);
        List<Booking> pastBookerBookings = query.setParameter("bookerId", request.getUserId())
                .setParameter("now", LocalDateTime.now())
                .setFirstResult(request.getFrom())
                .setMaxResults(request.getSize())
                .getResultList();

        Assertions.assertThat(pastBookerBookingsDtoInfo)
                .hasSameSizeAs(pastBookerBookings)
                .hasSize(1)
                .contains(BookingMapper.convertToBookingDtoInfo(booking6));
        Assertions.assertThat(pastBookerBookings).contains(booking6);
    }

    @Test
    void getAllRejectedOwnerTest() {
        GetAllBookingsRequest request = GetAllBookingsRequest.of(State.REJECTED, user2.getId(),
                true, 0, 1);
        List<BookingDtoInfo> rejectedOwnerBookingsDtoInfo = service.getAll(request);

        TypedQuery<Booking> query = em.createQuery("select b from Booking b " +
                "where b.item.owner.id = :ownerId and b.status = :status " +
                "order by b.start desc", Booking.class);
        List<Booking> rejectedOwnerBookings = query.setParameter("ownerId", request.getUserId())
                .setParameter("status", BookingStatus.REJECTED)
                .setFirstResult(request.getFrom())
                .setMaxResults(request.getSize())
                .getResultList();

        Assertions.assertThat(rejectedOwnerBookingsDtoInfo)
                .hasSameSizeAs(rejectedOwnerBookings)
                .hasSize(1)
                .contains(BookingMapper.convertToBookingDtoInfo(booking3));
        Assertions.assertThat(rejectedOwnerBookings).contains(booking3);
    }

    @Test
    void getAllRejectedBookerTest() {
        GetAllBookingsRequest request = GetAllBookingsRequest.of(State.REJECTED, user1.getId(),
                false, 0, 1);
        List<BookingDtoInfo> rejectedOwnerBookingsDtoInfo = service.getAll(request);

        TypedQuery<Booking> query = em.createQuery("select b from Booking b " +
                "where b.booker.id = :bookerId and b.status = :status " +
                "order by b.start desc", Booking.class);
        List<Booking> rejectedOwnerBookings = query.setParameter("bookerId", request.getUserId())
                .setParameter("status", BookingStatus.REJECTED)
                .setFirstResult(request.getFrom())
                .setMaxResults(request.getSize())
                .getResultList();

        Assertions.assertThat(rejectedOwnerBookingsDtoInfo)
                .hasSameSizeAs(rejectedOwnerBookings)
                .hasSize(1)
                .contains(BookingMapper.convertToBookingDtoInfo(booking3));
        Assertions.assertThat(rejectedOwnerBookings).contains(booking3);
    }

    @Test
    void getAllWaitingOwnerTest() {
        GetAllBookingsRequest request = GetAllBookingsRequest.of(State.WAITING, user1.getId(),
                true, 0, 1);
        List<BookingDtoInfo> waitingOwnerBookingsDtoInfo = service.getAll(request);

        TypedQuery<Booking> query = em.createQuery("select b from Booking b " +
                "where b.item.owner.id = :ownerId and b.status = :status " +
                "order by b.start desc", Booking.class);
        List<Booking> waitingOwnerBookings = query.setParameter("ownerId", request.getUserId())
                .setParameter("status", BookingStatus.WAITING)
                .setFirstResult(request.getFrom())
                .setMaxResults(request.getSize())
                .getResultList();

        Assertions.assertThat(waitingOwnerBookingsDtoInfo)
                .hasSameSizeAs(waitingOwnerBookings)
                .hasSize(1)
                .contains(BookingMapper.convertToBookingDtoInfo(booking2));
        Assertions.assertThat(waitingOwnerBookings).contains(booking2);
    }

    @Test
    void getAllWaitingBookerTest() {
        GetAllBookingsRequest request = GetAllBookingsRequest.of(State.WAITING, user2.getId(),
                false, 0, 1);
        List<BookingDtoInfo> waitingBookerBookingsDtoInfo = service.getAll(request);

        TypedQuery<Booking> query = em.createQuery("select b from Booking b " +
                "where b.booker.id = :bookerId and b.status = :status " +
                "order by b.start desc", Booking.class);
        List<Booking> waitingBookerBookings = query.setParameter("bookerId", request.getUserId())
                .setParameter("status", BookingStatus.WAITING)
                .setFirstResult(request.getFrom())
                .setMaxResults(request.getSize())
                .getResultList();

        Assertions.assertThat(waitingBookerBookingsDtoInfo)
                .hasSameSizeAs(waitingBookerBookings)
                .hasSize(1)
                .contains(BookingMapper.convertToBookingDtoInfo(booking2));
        Assertions.assertThat(waitingBookerBookings).contains(booking2);
    }

    @Test
    void getAllOwnerTest() {
        GetAllBookingsRequest request = GetAllBookingsRequest.of(State.ALL, user1.getId(),
                true, 0, 1);
        List<BookingDtoInfo> allOwnerBookingsDtoInfo = service.getAll(request);

        TypedQuery<Booking> query = em.createQuery("select b from Booking b " +
                "where b.item.owner.id = :ownerId " +
                "order by b.start desc", Booking.class);
        List<Booking> allOwnerBookings = query.setParameter("ownerId", request.getUserId())
                .setFirstResult(request.getFrom())
                .setMaxResults(request.getSize())
                .getResultList();

        Assertions.assertThat(allOwnerBookingsDtoInfo)
                .hasSameSizeAs(allOwnerBookings)
                .hasSize(1)
                .contains(BookingMapper.convertToBookingDtoInfo(booking2));
        Assertions.assertThat(allOwnerBookings).contains(booking2);
    }

    @Test
    void getAllBookerTest() {
        GetAllBookingsRequest request = GetAllBookingsRequest.of(State.ALL, user1.getId(),
                false, 0, 1);
        List<BookingDtoInfo> allBookerBookingsDtoInfo = service.getAll(request);

        TypedQuery<Booking> query = em.createQuery("select b from Booking b " +
                "where b.booker.id = :bookerId " +
                "order by b.start desc", Booking.class);
        List<Booking> allBookerBookings = query.setParameter("bookerId", request.getUserId())
                .setFirstResult(request.getFrom())
                .setMaxResults(request.getSize())
                .getResultList();

        Assertions.assertThat(allBookerBookingsDtoInfo)
                .hasSameSizeAs(allBookerBookings)
                .hasSize(1)
                .contains(BookingMapper.convertToBookingDtoInfo(booking4));
        Assertions.assertThat(allBookerBookings).contains(booking4);
    }
}
