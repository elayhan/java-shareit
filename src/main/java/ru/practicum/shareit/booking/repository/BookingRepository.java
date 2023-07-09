package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner as o " +
            "join b.booker as u " +
            "where b.id = ?2 and (o.id = ?1 or u.id = ?1)")
    Optional<Booking> findByIdAndOwnerOrBooker(Long userId, Long bookingId);

    Page<Booking> findAllByBookerIdOrderByStartDesc(Long userId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId,
                                                                             LocalDateTime start,
                                                                             LocalDateTime end,
                                                                             Pageable pageable);

    Page<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime start, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, BookingStatus status, Pageable pageable);


    Page<Booking> findAllByItemOwnerIdOrderByStartDesc(Long userId, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId,
                                                                                LocalDateTime start,
                                                                                LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime start, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long userId, BookingStatus status, Pageable pageable);

    @Query(value = "select b.* from bookings as b " +
            "join items as i on b.item_id = i.id " +
            "where b.item_id = :itemId " +
            "and i.owner_id = :ownerId " +
            "and b.status = 'APPROVED' " +
            "and b.start_date < current_timestamp " +
            "order by b.start_date desc " +
            "limit 1 ", nativeQuery = true)
    Booking findLastBooking(Long itemId, Long ownerId);

    @Query(value = "select b.* from bookings as b " +
            "join items as i on b.item_id = i.id " +
            "where b.item_id = :itemId " +
            "and i.owner_id = :ownerId " +
            "and b.status = 'APPROVED' " +
            "and b.start_date > current_timestamp " +
            "order by b.start_date " +
            "limit 1 ", nativeQuery = true)
    Booking findNextBooking(Long itemId, Long ownerId);

    @Query(value = "select b.* from bookings as b " +
            "where b.item_id = ?1 and b.booker_id = ?2 " +
            "and status = 'APPROVED' " +
            "and start_date < current_timestamp " +
            "limit 1", nativeQuery = true)
    Optional<Booking> checkBooked(Long itemId, Long bookerId);

}
