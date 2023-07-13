package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingOwnerDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingOwnerDtoTest {
    @Autowired
    private JacksonTester<BookingOwnerDto> json;

    private final BookingOwnerDto bookingOwnerDto = new BookingOwnerDto();

    @Test
    void bookingOwnerDtoTest() throws IOException {
        bookingOwnerDto.setId(1L);
        bookingOwnerDto.setBookerId(2L);

        JsonContent<BookingOwnerDto> result = json.write(bookingOwnerDto);

        assertThat(result).extractingJsonPathNumberValue("$.id", bookingOwnerDto.getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.bookerId", bookingOwnerDto.getBookerId().intValue());
    }

}
