package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
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
        return userMapper.toUserDto(repository.save(user));
    }

    @Override
    public UserDto getUser(Long id) {
        return userMapper.toUserDto(getUserFromRepository(id));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userMapper.toListUserDto(repository.findAll());
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {

        User updUser = getUserFromRepository(id);
        if (userDto.getEmail() != null) {
            updUser.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            updUser.setName(userDto.getName());
        }

        return userMapper.toUserDto(repository.save(updUser));
    }

    private User getUserFromRepository(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    @Override
    public void deleteUser(Long id) {
        repository.deleteById(id);
    }

}
