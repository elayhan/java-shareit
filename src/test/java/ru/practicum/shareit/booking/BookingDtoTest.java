package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;
    private final BookingDto bookingDto = new BookingDto();

    @Test
    void bookingDtoTest() throws IOException {
        setBookingDto(2L,
                2L,
                LocalDateTime.of(2000, 1, 1, 0, 0),
                LocalDateTime.of(2000, 1, 1, 14, 1, 59),
                BookingStatus.WAITING);

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingDto.getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(bookingDto.getItemId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2000-01-01T00:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2000-01-01T14:01:59");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(bookingDto.getStatus().toString());
    }

    private void setBookingDto(Long id, Long itemId, LocalDateTime start, LocalDateTime end, BookingStatus status) {
        bookingDto.setId(id);
        bookingDto.setItemId(itemId);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setStatus(status);
    }

}
