package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadBookingTimeException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotSupportState;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceTest {
    private final BookingService service;
    private final UserService userService;
    private final ItemService itemService;

    UserDto user;
    ItemDto item;

    @BeforeEach
    void beforeEach() {
        user = userService.createUser(UserDto.builder()
                .name("name")
                .email("e@mail.com")
                .build());
        item = itemService.createItem(user.getId(), ItemDto.builder()
                .name("item")
                .owner(user.getId())
                .available(true)
                .build());

    }

    @Test
    void createBookingTest() {
        userService.createUser(UserDto.builder()
                .name("name2")
                .email("a@mail.com")
                .build());

        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().minusMinutes(1));
        bookingDto.setEnd(LocalDateTime.now().plusMinutes(20));
        bookingDto.setItemId(item.getId());

        BookingDto createdBooking = service.createBooking(2L, bookingDto);

        assertEquals(1L, createdBooking.getId());
        assertEquals(1L, createdBooking.getItem().getId());
        assertEquals(BookingStatus.WAITING, createdBooking.getStatus());
        assertEquals(2L, createdBooking.getBooker().getId());
    }

    @Test
    void createBookingNotValidTimeTest() {
        userService.createUser(UserDto.builder()
                .name("name2")
                .email("a@mail.com")
                .build());

        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusMinutes(3));
        bookingDto.setEnd(LocalDateTime.now().plusMinutes(1));
        bookingDto.setItemId(item.getId());

        assertThrows(BadBookingTimeException.class, () -> service.createBooking(2L, bookingDto));
    }

    @Test
    void createBookingOwnerTest() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusMinutes(1));
        bookingDto.setEnd(LocalDateTime.now().plusMinutes(2));
        bookingDto.setItemId(item.getId());

        assertThrows(NotFoundException.class, () -> service.createBooking(1L, bookingDto));
    }

    @Test
    void createBookingItemNotAvailableTest() {
        createBookingTest();

        item.setAvailable(false);
        itemService.updateItem(1L, 1L, item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusMinutes(1));
        bookingDto.setEnd(LocalDateTime.now().plusMinutes(6));
        bookingDto.setItemId(item.getId());
        assertThrows(NotAvailableException.class, () -> service.createBooking(2L, bookingDto));
    }

    @Test
    void approveBookingNoOwnerTest() {
        createBookingTest();
        assertThrows(NotFoundException.class, () -> service.approveBooking(2L, 1L, true));
    }

    @Test
    void approveBookingApprovedTest() {
        createBookingTest();
        service.approveBooking(1L, 1L, true);
        assertThrows(NotAvailableException.class, () -> service.approveBooking(1L, 1L, true));
    }

    @Test
    void getBookingTest() {
        createBookingTest();
        BookingDto bookingDto = service.getBooking(1L, 1L);
        assertNotNull(bookingDto);
        assertEquals(1L, bookingDto.getId());
        assertEquals(1L, bookingDto.getItem().getId());
        assertEquals(2L, bookingDto.getBooker().getId());
        assertEquals(BookingStatus.WAITING, bookingDto.getStatus());

        service.approveBooking(1L, 1L, false);
        bookingDto = service.getBooking(1L, 1L);
        assertEquals(BookingStatus.REJECTED, bookingDto.getStatus());
    }

    @Test
    void getBookingNotExistsTest() {
        assertThrows(NotFoundException.class, () -> service.getBooking(1L, 1L));
    }

    @Test
    void getAllBookingByStateAllTest() {
        createBookingTest();
        List<BookingDto> bookings = service.getAllBookingsByState(2L, "ALL", 0, 10);
        assertEquals(1, bookings.size());
    }

    @Test
    void getAllBookingByStateCurrentTest() {
        createBookingTest();
        List<BookingDto> bookings = service.getAllBookingsByState(2L, "CURRENT", 0, 10);
        assertEquals(1, bookings.size());
    }

    @Test
    void getAllBookingByStatePastTest() {
        userService.createUser(UserDto.builder()
                .name("name2")
                .email("a@mail.com")
                .build());

        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().minusMinutes(3));
        bookingDto.setEnd(LocalDateTime.now().minusMinutes(2));
        bookingDto.setItemId(item.getId());

        BookingDto createdBooking = service.createBooking(2L, bookingDto);

        List<BookingDto> bookings = service.getAllBookingsByState(2L, "PAST", 0, 10);
        assertEquals(1, bookings.size());
    }

    @Test
    void getAllBookingByStateFutureTest() {
        userService.createUser(UserDto.builder()
                .name("name2")
                .email("a@mail.com")
                .build());

        List<BookingDto> bookings = service.getAllBookingsByState(2L, "FUTURE", 0, 10);
        assertEquals(0, bookings.size());

        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));
        bookingDto.setItemId(item.getId());

        BookingDto createdBooking = service.createBooking(2L, bookingDto);

        bookings = service.getAllBookingsByState(2L, "FUTURE", 0, 10);
        assertEquals(1, bookings.size());
    }

    @Test
    void getAllBookingByStateWaitingTest() {
        createBookingTest();
        List<BookingDto> bookings = service.getAllBookingsByState(2L, "WAITING", 0, 10);
        assertEquals(1, bookings.size());
    }

    @Test
    void getAllBookingByStateRejectedTest() {
        createBookingTest();
        List<BookingDto> bookings = service.getAllBookingsByState(2L, "REJECTED", 0, 10);
        assertEquals(0, bookings.size());

        service.approveBooking(1L, 1L, false);
        bookings = service.getAllBookingsByState(2L, "REJECTED", 0, 10);
        assertEquals(1, bookings.size());
    }

    @Test
    void getAllBookingByStateUnknownTest() {
        createBookingTest();
        assertThrows(NotSupportState.class, () -> service.getAllBookingsByState(2L, "UNK", 0, 1));
    }

    @Test
    void getAllBookingsByOwnedAndStateAllTest() {
        createBookingTest();
        List<BookingDto> bookings = service.getAllBookingsByOwnedAndState(1L, "ALL", 0, 10);
        assertEquals(1, bookings.size());
    }

    @Test
    void getAllBookingsByOwnedAndStateCurrentTest() {
        createBookingTest();
        List<BookingDto> bookings = service.getAllBookingsByOwnedAndState(1L, "CURRENT", 0, 10);
        assertEquals(1, bookings.size());
    }

    @Test
    void getAllBookingsByOwnedAndStatePastTest() {
        getAllBookingByStatePastTest();
        List<BookingDto> bookings = service.getAllBookingsByOwnedAndState(1L, "PAST", 0, 10);
        assertEquals(1, bookings.size());
    }

    @Test
    void getAllBookingsByOwnedAndStateFutureTest() {
        getAllBookingByStateFutureTest();
        List<BookingDto> bookings = service.getAllBookingsByOwnedAndState(1L, "FUTURE", 0, 10);
        assertEquals(1, bookings.size());
    }

    @Test
    void getAllBookingsByOwnedAndStateWaitingTest() {
        createBookingTest();
        List<BookingDto> bookings = service.getAllBookingsByOwnedAndState(1L, "WAITING", 0, 10);
        assertEquals(1, bookings.size());

        bookings = service.getAllBookingsByOwnedAndState(2L, "WAITING", 0, 10);
        assertEquals(0, bookings.size());
    }

    @Test
    void getAllBookingsByOwnedAndStateRejectedTest() {
        createBookingTest();
        List<BookingDto> bookings = service.getAllBookingsByOwnedAndState(1L, "REJECTED", 0, 10);
        assertEquals(0, bookings.size());

        service.approveBooking(1L, 1L, false);

        bookings = service.getAllBookingsByOwnedAndState(1L, "REJECTED", 0, 10);
        assertEquals(1, bookings.size());
    }

    @Test
    void getAllBookingsByOwnedAndStateUnknownTest() {
        createBookingTest();
        assertThrows(NotSupportState.class, () -> service.getAllBookingsByOwnedAndState(1L, "UNK", 0, 10));
    }
}
