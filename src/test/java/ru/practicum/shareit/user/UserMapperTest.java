package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.mapper.UserMapper;

import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserMapperTest {
    private final UserMapper mapper;

    @Test
    void toUserDtoNullTest() {
        assertNull(mapper.toUserDto(null));
    }

    @Test
    void toUserNullTest() {
        assertNull(mapper.toUser(null));
    }

    @Test
    void toListUserDtoNullTest() {
        assertNull(mapper.toListUserDto(null));
    }
}
