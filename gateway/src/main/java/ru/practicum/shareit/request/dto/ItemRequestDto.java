package ru.practicum.shareit.request.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class ItemRequestDto {
    @NotBlank
    private String description;
}
