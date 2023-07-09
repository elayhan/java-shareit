package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestServiceTest {
    private final ItemRequestService service;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    void createRequestNoUserTest() {
        assertThrows(NotFoundException.class, () -> service.createRequest(1l, new ItemRequestDto()));
    }

    @Test
    void createRequestTest() {
        userService.createUser(UserDto.builder()
                .name("name")
                .email("111@222.com")
                .build());

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("description");

        ItemRequestDto createdItemRequestDto = service.createRequest(1L, itemRequestDto);

        assertNotNull(createdItemRequestDto);
        assertEquals(1L, createdItemRequestDto.getId());
        assertEquals("description", createdItemRequestDto.getDescription());
        assertNotNull(createdItemRequestDto.getCreated());
    }

    @Test
    void getOwnerRequestsTest() {
        createRequestTest();

        List<ItemRequestDto> items = service.getOwnerRequests(1L);
        assertEquals(1, items.size());

        userService.createUser(UserDto.builder()
                .name("name2")
                .email("222@222.com")
                .build());

        items = service.getOwnerRequests(2L);

        assertEquals(0, items.size());

        assertThrows(NotFoundException.class, () -> service.getOwnerRequests(3L));

    }

    @Test
    void getAllRequestsTest() {
        userService.createUser(UserDto.builder()
                .name("name")
                .email("111@222.com")
                .build());

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("description");

        for (int i = 0; i < 20; i++) {
            itemRequestDto.setDescription("description" + i);
            service.createRequest(1L, itemRequestDto);
        }

        userService.createUser(UserDto.builder()
                .name("name2")
                .email("222@222.com")
                .build());

        List<ItemRequestDto> items = service.getAllRequests(2L, 0, 11);
        assertEquals(11, items.size());

        items = service.getAllRequests(2L, 1, 10);
        assertEquals(10, items.size());

        items = service.getAllRequests(2L, 2, 10);
        assertEquals(0, items.size());

    }

    @Test
    void getRequestByIdTest() {
        createRequestTest();
        ItemRequestDto itemRequestDto = service.getRequestById(1L, 1L);
        assertNotNull(itemRequestDto);
        assertEquals(1, itemRequestDto.getId());
        assertEquals("description", itemRequestDto.getDescription());
    }

    @Test
    void getRequestByIdItemListTest() {
        createRequestTest();
        userService.createUser(UserDto.builder()
                .name("name2")
                .email("222@222.com")
                .build());

        ItemDto itemDto = ItemDto.builder()
                .name("item")
                .available(true)
                .owner(2L)
                .requestId(1L)
                .build();
        itemService.createItem(1L, itemDto);

        ItemRequestDto itemRequestDto = service.getRequestById(1L, 1L);
        assertEquals(1, itemRequestDto.getItems().size());
        assertEquals("item", itemRequestDto.getItems().get(0).getName());
    }
}
