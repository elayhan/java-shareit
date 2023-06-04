package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(repository.createUser(user));
    }

    @Override
    public UserDto getUser(Long id) {
        return UserMapper.toUserDto(repository.getUser(id));
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> result = new ArrayList<>();
        repository.getAllUsers().forEach(user -> result.add(UserMapper.toUserDto(user)));
        return result;
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User updUser = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(repository.updateUser(id, updUser));
    }

    @Override
    public void deleteUser(Long id) {
        repository.deleteUser(id);
    }
}
