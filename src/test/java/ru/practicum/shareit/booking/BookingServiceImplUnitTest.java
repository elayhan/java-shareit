package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BadBookingTimeException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotSupportState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplUnitTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository repository;
    @InjectMocks
    private BookingServiceImpl service;

    @Nested
    class CreateTests {
        private BookingDto bookingDto = new BookingDto();

        @Test
        void createBookingBadBookingExTest() {
            BookingDto bookingDto = new BookingDto();
            bookingDto.setStart(LocalDateTime.now());
            bookingDto.setEnd(LocalDateTime.now().minusHours(1));

            assertThatThrownBy(() -> service.createBooking(1L, bookingDto))
                    .isInstanceOf(BadBookingTimeException.class)
                    .hasMessage("Время бо-бо");
        }

        @Test
        void createBookingOwnerTest() {
            User user = new User();
            user.setId(1L);

            Item item = new Item();
            item.setOwner(user);

            when(userRepository.findById(any())).thenReturn(Optional.of(user));
            when(itemRepository.findById(any())).thenReturn(Optional.of(item));

            BookingDto bookingDto = new BookingDto();
            bookingDto.setStart(LocalDateTime.of(2000, 1, 1, 0, 0));
            bookingDto.setEnd(LocalDateTime.of(2001, 1, 1, 1, 1));

            assertThatThrownBy(() -> service.createBooking(1L, bookingDto))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Своей вещью пользоваться нельзя!111");
        }

        @Test
        void createBookingItemNotAvailableTest() {
            User user = new User();
            user.setId(2L);

            Item item = new Item();
            item.setOwner(user);
            item.setAvailable(false);


            bookingDto.setStart(LocalDateTime.of(2000, 1, 1, 0, 0));
            bookingDto.setEnd(LocalDateTime.of(2001, 1, 1, 1, 1));

            when(userRepository.findById(any())).thenReturn(Optional.of(user));
            when(itemRepository.findById(any())).thenReturn(Optional.of(item));

            assertThatThrownBy(() -> service.createBooking(1L, bookingDto))
                    .isInstanceOf(NotAvailableException.class)
                    .hasMessage("Вещь не доступна для бронирования");
        }

        @Test
        void createBookingItemNotExistsTest() {
            when(userRepository.findById(any())).thenReturn(Optional.of(new User()));

            bookingDto.setStart(LocalDateTime.of(2000, 1, 1, 0, 0));
            bookingDto.setEnd(LocalDateTime.of(2001, 1, 1, 1, 1));
            assertThatThrownBy(() -> service.createBooking(1L, bookingDto))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Вещь тю-тю");
        }
    }

    @Nested
    class UpdateTests {

        private User user = new User();
        private Item item = new Item();

        private Booking booking = new Booking();

        @BeforeEach
        void beforeEach() {
            user.setId(1L);
            item.setOwner(user);
            booking.setItem(item);
            booking.setStatus(BookingStatus.APPROVED);
            when(repository.findById(any())).thenReturn(Optional.of(booking));
        }

        @Test
        void approveBookingNotOwnerItemTest() {
            assertThatThrownBy(() -> service.approveBooking(2L, any(), false))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Нельзя подтвердить это бронирование");
        }

        @Test
        void approveBookingFinalStatusTest() {
            assertThatThrownBy(() -> service.approveBooking(1L, any(), false))
                    .isInstanceOf(NotAvailableException.class)
                    .hasMessage("Статус конечный");
        }
    }

    @Nested
    class ReadTests {
        @Test
        void getBookingNotExistsTest() {
            assertThatThrownBy(() -> service.getBooking(1L, 1L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Бронирорвание не найдено");
        }

        @Test
        void getAllBookingByStateTest() {
            when(userRepository.findById(any())).thenReturn(Optional.of(new User()));
            assertThatThrownBy(() -> service.getAllBookingsByState(1L, "UnknownState", 0, 10))
                    .isInstanceOf(NotSupportState.class)
                    .hasMessage("Unknown state: UNSUPPORTED_STATUS");
        }

        @Test
        void getAllBookingByOwnedAndStateTest() {
            when(userRepository.findById(any())).thenReturn(Optional.of(new User()));
            assertThatThrownBy(() -> service.getAllBookingsByOwnedAndState(1L, "UnknownState", 0, 10))
                    .isInstanceOf(NotSupportState.class)
                    .hasMessage("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
