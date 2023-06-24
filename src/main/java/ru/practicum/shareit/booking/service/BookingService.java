package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(Long userId, BookingDto bookingDto);

    BookingDto approveBooking(Long userId, Long bookingId, Boolean isApproved);

    BookingDto getBooking(Long userId, Long bookingId);

    List<BookingDto> getAllBookingsByState(Long userId, String state);

    List<BookingDto> getAllBookingsByOwnedAndState(Long userid, String state);

}
