package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.ToString;
import ru.practicum.shareit.exception.ValidateException;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@ToString
public class UserDto {
    private Long id;
    @NotBlank(groups = ValidateException.OnCreate.class)
    private String name;
    @Email(groups = {ValidateException.OnUpdate.class, ValidateException.OnCreate.class})
    @NotBlank(groups = ValidateException.OnCreate.class)
    private String email;
}
