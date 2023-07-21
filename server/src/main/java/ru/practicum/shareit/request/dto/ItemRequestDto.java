package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.CustomJsonFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ItemRequestDto {
    private Long id;
    private String description;

    @JsonFormat(pattern = CustomJsonFormat.PATTERN)
    private LocalDateTime created;

    private List<ItemDto> items;

}
