package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(repository.createUser(user));
    }

    @Override
    public UserDto getUser(Long id) {
        return userMapper.toUserDto(repository.getUser(id));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userMapper.toListUserDto(repository.getAllUsers());
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User updUser = userMapper.toUser(userDto);
        return userMapper.toUserDto(repository.updateUser(id, updUser));
    }

    @Override
    public void deleteUser(Long id) {
        repository.deleteUser(id);
    }
}
