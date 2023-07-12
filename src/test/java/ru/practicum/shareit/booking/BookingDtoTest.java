package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    JacksonTester<BookingDto> json;

    @Test
    void bookingDtoTest() throws IOException {
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("e@mail.com");

        Item item = new Item();
        item.setId(2L);
        item.setDescription("description");
        item.setName("ItemName");
        item.setOwner(user);
        item.setAvailable(true);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(2L);
        bookingDto.setItemId(2L);
        bookingDto.setItem(item);
        bookingDto.setStart(LocalDateTime.of(2000, 1, 1, 0, 0));
        bookingDto.setEnd(LocalDateTime.of(2000, 1, 1, 14, 1, 59));
        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setBooker(user);


        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2000-01-01T00:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2000-01-01T14:01:59");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");

        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("ItemName");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("description");

        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("e@mail.com");
    }
}
