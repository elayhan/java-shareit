package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadBookingTimeException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotSupportState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper mapper;

    @Override
    public BookingDto createBooking(Long userId, BookingDto bookingDto) {

        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new BadBookingTimeException("Время бо-бо");
        }

        User user = getUser(userId);
        Item item = getItem(bookingDto.getItemId());

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Своей вещью пользоваться нельзя!111");
        }

        if (!item.getAvailable()) {
            throw new NotAvailableException("Почему-то нельзя забронировать, забронированную вещь");
        }

        Booking booking = mapper.toBooking(bookingDto, item, user);
        booking.setStatus(BookingStatus.WAITING);
        return mapper.toBookingDto(repository.save(booking));
    }

    @Override
    public BookingDto approveBooking(Long userId, Long bookingId, Boolean isApproved) {
        Booking booking = getBookingFromRepository(bookingId);

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Нельзя подтвердить это бронирование");
        }

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new NotAvailableException("Статус конечный");
        }

        booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return mapper.toBookingDto(repository.save(booking));
    }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        return mapper.toBookingDto(repository.findByIdAndOwnerOrBooker(userId, bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирорвание не найдено")));
    }

    @Override
    public List<BookingDto> getAllBookingsByState(Long userId, String state) {
        getUser(userId);
        LocalDateTime now = LocalDateTime.now();

        Map<String, Supplier<List<BookingDto>>> strategyMap = Map.of(
                "ALL", () ->
                        mapper.toListBookingDto(repository
                                .findAllByBookerIdOrderByStartDesc(userId)),
                "CURRENT", () ->
                        mapper.toListBookingDto(repository
                                .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now)),
                "PAST", () ->
                        mapper.toListBookingDto(repository
                                .findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now)),
                "FUTURE", () ->
                        mapper.toListBookingDto(repository
                                .findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now)),
                "WAITING", () ->
                        mapper.toListBookingDto(repository
                                .findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING)),
                "REJECTED", () ->
                        mapper.toListBookingDto(repository
                                .findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED))
        );


        return Optional.ofNullable(strategyMap.get(state))
                .orElseThrow(() -> new NotSupportState("Unknown state: UNSUPPORTED_STATUS")).get();
    }

    @Override
    public List<BookingDto> getAllBookingsByOwnedAndState(Long userId, String state) {
        getUser(userId);
        LocalDateTime now = LocalDateTime.now();

        Map<String, Supplier<List<BookingDto>>> strategyMap = Map.of(
                "ALL", () ->
                        mapper.toListBookingDto(repository.findAllByItemOwnerIdOrderByStartDesc(userId)),
                "CURRENT", () ->
                        mapper.toListBookingDto(repository
                                .findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now)),
                "PAST", () ->
                        mapper.toListBookingDto(repository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now)),
                "FUTURE", () ->
                        mapper.toListBookingDto(repository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now)),
                "WAITING", () ->
                        mapper.toListBookingDto(repository
                                .findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING)),
                "REJECTED", () ->
                        mapper.toListBookingDto(repository
                                .findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED))
        );


        return Optional.ofNullable(strategyMap.get(state))
                .orElseThrow(() -> new NotSupportState("Unknown state: UNSUPPORTED_STATUS")).get();

    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден."));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь тю-тю"));
    }

    private Booking getBookingFromRepository(Long bookingId) {
        return repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Нельзя подтвердить это бронирование"));
    }

}
