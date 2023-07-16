package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@ToString
public class CommentDto {
    @NotBlank
    private String text;
}
