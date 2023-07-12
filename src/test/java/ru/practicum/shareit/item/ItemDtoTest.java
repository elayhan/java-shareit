package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingOwnerDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {
    @Autowired
    JacksonTester<ItemDto> json;

    @Test
    void itemDtoTest() throws IOException {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("commentText");
        commentDto.setItemId(1L);
        commentDto.setCreated(LocalDateTime.of(2000, 12, 31, 23, 59, 59));
        commentDto.setAuthorName("authorName");

        BookingOwnerDto lastBooking = new BookingOwnerDto();
        lastBooking.setId(1L);
        lastBooking.setBookerId(2L);

        BookingOwnerDto nextBooking = new BookingOwnerDto();
        nextBooking.setId(2L);
        nextBooking.setBookerId(3L);

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("ItemName")
                .description("description")
                .available(true)
                .owner(1L)
                .requestId(2L)
                .comments(List.of(commentDto))
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.owner").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(3);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("ItemName");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("commentText");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created").isEqualTo("2000-12-31T23:59:59");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("authorName");

        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
    }
}
