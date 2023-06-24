package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.CustomHeader;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;

    @PostMapping
    public BookingDto createBooking(@RequestHeader(CustomHeader.USER_ID) Long userId,
                                    @Valid @RequestBody BookingDto bookingDto) {
        return service.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader(CustomHeader.USER_ID) Long userId,
                                     @PathVariable Long bookingId,
                                     @RequestParam Boolean approved) {
        return service.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(CustomHeader.USER_ID) Long userId,
                                 @PathVariable Long bookingId) {
        return service.getBooking(userId, bookingId);
    }

    @GetMapping()
    public List<BookingDto> getAllBookingByState(@RequestHeader(CustomHeader.USER_ID) Long userId,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        return service.getAllBookingsByState(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingByOwnerAndState(@RequestHeader(CustomHeader.USER_ID) Long userId,
                                                         @RequestParam(defaultValue = "ALL") String state) {
        return service.getAllBookingsByOwnedAndState(userId, state);
    }
}
