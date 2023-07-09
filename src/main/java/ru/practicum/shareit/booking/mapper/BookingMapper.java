package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOwnerDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper
public interface BookingMapper {

    BookingDto toBookingDto(Booking booking);

    @Mapping(target = "item", source = "item")
    @Mapping(target = "booker", source = "user")
    @Mapping(target = "id", source = "bookingDto.id")
    Booking toBooking(BookingDto bookingDto, Item item, User user);

    List<BookingDto> toListBookingDto(Page<Booking> bookings);

    @Mapping(target = "bookerId", source = "booker.id")
    BookingOwnerDto toBookingOwnerDto(Booking booking);

}
