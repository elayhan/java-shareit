package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.ToString;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@ToString
public class ItemDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private Long owner;
    private Long requestId;
    private BookItemRequestDto lastBooking;
    private BookItemRequestDto nextBooking;
    private List<CommentDto> comments;
}
