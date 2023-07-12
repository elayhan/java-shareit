package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.CustomJsonFormat;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class BookingDto {
    private long id;

    @NotNull
    @FutureOrPresent
    @JsonFormat(pattern = CustomJsonFormat.PATTERN)
    private LocalDateTime start;

    @NotNull
    @FutureOrPresent
    @JsonFormat(pattern = CustomJsonFormat.PATTERN)
    private LocalDateTime end;

    @NotNull
    private Long itemId;
    private Item item;
    private User booker;
    private BookingStatus status;
}
