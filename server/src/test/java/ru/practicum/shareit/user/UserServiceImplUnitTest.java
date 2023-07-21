package ru.practicum.shareit.user;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplUnitTest {
    @Mock
    private UserRepository repository;
    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserServiceImpl service;

    private UserDto user = UserDto.builder()
            .id(1L)
            .name("name")
            .email("e@mail.com")
            .build();

    @Nested
    class CreateTests {
        @Test
        void createUserTest() {
            when(mapper.toUserDto(any())).thenReturn(user);

            UserDto createdUser = service.createUser(user);
            assertThat(createdUser).isEqualTo(user);
        }
    }

    @Nested
    class ReadTests {
        @Test
        void getUserTest() {
            when(mapper.toUserDto(any())).thenReturn(user);
            UserDto createdUser = service.createUser(user);
            assertThat(createdUser).isEqualTo(user);
        }

        @Test
        void getUserNotExistsTest() {
            assertThatThrownBy(() -> service.getUser(1L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Пользователь не найден");
        }

        @Test
        void getAllUsers() {
            when(mapper.toListUserDto(any())).thenReturn(List.of(user, user));

            List<UserDto> users = service.getAllUsers();

            assertThat(users).isNotEmpty();
            assertThat(users.get(0)).isEqualTo(user);
        }
    }

    @Nested
    class UpdateTests {
        @Test
        void updateUserTest() {
            when(mapper.toUserDto(any())).thenReturn(user);

            UserDto createdUser = service.createUser(user);
            assertThat(createdUser).isEqualTo(user);
        }

        @Test
        void updateUserNotExistsTest() {
            assertThatThrownBy(() -> service.updateUser(1L, user))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Пользователь не найден");
        }
    }


}
