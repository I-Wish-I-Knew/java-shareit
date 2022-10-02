package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingStorage extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long bookerId, Sort sort);

    Optional<Booking> findBookingByIdAndBookerId(Long id, Long bookerId);

    Optional<Booking> findBookingByIdAndItemOwnerId(Long id, Long ownerId);

    List<Booking> findByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId, Long bookerId,
                                                               BookingStatus status, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.booker.id = :bookerId " +
            "and b.start <= :now " +
            "and b.end > :now " +
            "order by b.start desc")
    List<Booking> findByBookerIdAndDateBetweenStartAndEnd(@Param("bookerId") Long bookerId,
                                                          @Param("now") LocalDateTime now);

    List<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStatusEquals(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findByItemOwnerId(Long ownerId, Sort sort);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :ownerId " +
            "and b.start <= :now " +
            "and b.end > :now " +
            "order by b.start desc")
    List<Booking> findByItemOwnerIdAndDateBetweenStartAndEnd(@Param("ownerId") Long ownerId,
                                                             @Param("now") LocalDateTime now);

    List<Booking> findByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime now, Sort sort);

    List<Booking> findByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime now, Sort sort);

    List<Booking> findByItemOwnerIdAndStatusEquals(Long ownerId, BookingStatus status, Sort sort);

    @Query("select count(b)>0 from Booking b " +
            "where b.item.id = :itemId " +
            "and b.start < :end " +
            "and b.end > :start " +
            "and b.status = :status")
    boolean reservedForDates(@Param("itemId") Long itemId,
                             @Param("end") LocalDateTime end,
                             @Param("start") LocalDateTime start,
                             @Param("status") BookingStatus status);

    @Query(value = "select b.*," +
            "u.*, " +
            "i.* " +
            "from bookings b " +
            "left join users u on b.booker_id = u.user_id " +
            "left join items i on b.item_id = i.item_id " +
            "where b.item_id = :itemId " +
            "and i.owner_id = :ownerId " +
            "and b.end_date < :now " +
            "order by b.end_date desc " +
            "limit 1", nativeQuery = true)
    Optional<Booking> findLastBooking(@Param("itemId") Long itemId,
                                      @Param("now") LocalDateTime now,
                                      @Param("ownerId") Long ownerId);

    @Query(value = "select b.*," +
            "u.*, " +
            "i.* " +
            "from bookings b " +
            "left join users u on b.booker_id = u.user_id " +
            "left join items i on b.item_id = i.item_id " +
            "where b.item_id = :itemId " +
            "and i.owner_id = :ownerId " +
            "and b.start_date > :now " +
            "order by b.start_date " +
            "limit 1", nativeQuery = true)
    Optional<Booking> findNextBooking(@Param("itemId") Long itemId,
                                      @Param("now") LocalDateTime now,
                                      @Param("ownerId") Long ownerId);
}
