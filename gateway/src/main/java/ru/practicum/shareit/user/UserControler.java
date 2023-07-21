package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserControler {
    private final UserClient client;

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated(ValidateException.OnCreate.class) @RequestBody UserDto userDto) {
        log.info("Creating user {}", userDto);
        return client.createUser(userDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Get all users");
        return client.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        log.info("Get user by ID = {}", userId);
        return client.getUserById(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable long userId,
                                             @Validated(ValidateException.OnUpdate.class) @RequestBody UserDto userDto) {
        log.info("Update user by id = {}, set: {}", userId, userDto.toString());
        return client.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable long userId) {
        return client.deleteUserById(userId);
    }

}
