package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto getUser(Long id);

    List<UserDto> getAllUsers();

    UserDto updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);
}
