package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;
import util.CustomJsonFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@ToString
public class BookItemRequestDto {
    private long itemId;

    @NotNull
    @FutureOrPresent
    @JsonFormat(pattern = CustomJsonFormat.PATTERN)
    private LocalDateTime start;

    @NotNull
    @Future
    @JsonFormat(pattern = CustomJsonFormat.PATTERN)
    private LocalDateTime end;
}