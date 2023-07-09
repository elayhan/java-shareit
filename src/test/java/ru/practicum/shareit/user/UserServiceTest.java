package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceTest {
    private final UserService userService;

    UserDto userDto = UserDto.builder()
            .name("name")
            .email("e@mail.com")
            .build();

    @Test
    void getUsersEmptyTest() {
        List<UserDto> users = userService.getAllUsers();
        assertEquals(0, users.size());
    }

    @Test
    void createUserTest() {
        UserDto createdUser = userService.createUser(userDto);
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());

        UserDto getUser = userService.getUser(createdUser.getId());
        assertNotNull(getUser);
        assertEquals(userDto.getName(), getUser.getName());
        assertEquals(userDto.getEmail(), getUser.getEmail());
    }

    @Test
    void createUserInvalidEmailTest() {
        UserDto userInvalidEmail = UserDto.builder()
                .name("name")
                .email("@@@@")
                .build();

        assertThrows(ConstraintViolationException.class, () -> userService.createUser(userInvalidEmail));
    }

    @Test
    void createUserEmptyNameTest() {
        UserDto userEmptyName = UserDto.builder()
                .email("1@1.com")
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> userService.createUser(userEmptyName));
    }

    @Test
    void createUserDoubleEmail() {
        userService.createUser(userDto);

        assertThrows(DataIntegrityViolationException.class, () -> userService.createUser(userDto));
    }

    @Test
    void getUserNotExists() {
        assertThrows(NotFoundException.class, () -> userService.getUser(-99L));
    }

    @Test
    void getAllUsersTwoTest() {
        userService.createUser(userDto);
        userDto.setEmail("111@222.ru");
        userService.createUser(userDto);

        List<UserDto> users = userService.getAllUsers();

        assertEquals(2, users.size());
        assertEquals("e@mail.com", users.get(0).getEmail());
        assertEquals("111@222.ru", users.get(1).getEmail());
    }

    @Test
    void updateUserTest() {
        userService.createUser(userDto);
        userDto.setEmail("111@222.com");
        userDto.setName(null);
        UserDto updatedUser = userService.updateUser(1L, userDto);

        assertEquals("111@222.com", updatedUser.getEmail());
        assertEquals("name", updatedUser.getName());

        userDto.setEmail(null);
        userDto.setName("updatedName");
        updatedUser = userService.updateUser(1L, userDto);

        assertEquals("111@222.com", updatedUser.getEmail());
        assertEquals("updatedName", updatedUser.getName());
    }

    @Test
    void updateUserNotExists() {
        assertThrows(NotFoundException.class, () -> userService.updateUser(1L, userDto));
    }

    @Test
    void deleteUser() {
        userService.createUser(userDto);
        List<UserDto> users = userService.getAllUsers();
        assertEquals(1, users.size());
        userService.deleteUser(1L);
        users = userService.getAllUsers();
        assertEquals(0, users.size());
    }

    @Test
    void deleteNotExistsUser() {
        assertThrows(EmptyResultDataAccessException.class, () -> userService.deleteUser(-99L));
    }

}
