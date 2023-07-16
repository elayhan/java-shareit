package ru.practicum.shareit.item;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplUnitTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository repository;

    @InjectMocks
    private ItemServiceImpl service;

    @Nested
    class CreateTests {
        @Test
        void createItemUserNotExistTest() {
            when(userRepository.findById(any())).thenThrow(new NotFoundException("Пользователь не найден"));

            assertThatThrownBy(() -> service.createItem(1L, any()))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Пользователь не найден");
        }

    }

    @Nested
    class ReadTests {
        @Test
        void getItemNotExistsTest() {
            when(repository.findById(any())).thenThrow(new NotFoundException("Вещь не найдена"));
            assertThatThrownBy(() -> service.getItem(1L, 1L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Вещь не найдена");
        }
    }
}
