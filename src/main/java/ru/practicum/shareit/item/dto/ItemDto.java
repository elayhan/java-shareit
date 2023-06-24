package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.coyote.Request;
import ru.practicum.shareit.booking.dto.BookingOwnerDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private Long owner;
    private Request request;
    private BookingOwnerDto lastBooking;
    private BookingOwnerDto nextBooking;
    private List<CommentDto> comments;
}
