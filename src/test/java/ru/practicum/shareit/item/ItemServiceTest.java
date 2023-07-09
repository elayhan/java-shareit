package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotBookedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceTest {
    private final ItemService service;
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private final BookingService bookingService;

    ItemDto itemDto = ItemDto.builder()
            .name("item")
            .available(true)
            .build();

    @Test
    void createItemInvalidUserTest() {
        assertThrows(NotFoundException.class, () -> service.createItem(1L, itemDto));
    }

    @Test
    void createItemTest() {
        UserDto user = UserDto.builder()
                .name("user")
                .email("user@email.com")
                .build();
        userService.createUser(user);

        ItemDto item = service.createItem(1L, itemDto);

        assertNotNull(item);
        assertEquals(1L, item.getId());
        assertEquals("item", item.getName());
        assertEquals(1L, item.getOwner());
    }

    @Test
    void createRequestItemTest() {
        UserDto user = UserDto.builder()
                .name("user")
                .email("user@email.com")
                .build();
        userService.createUser(user);

        user.setEmail("user2@email.com");
        userService.createUser(user);

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("item1");

        itemRequestService.createRequest(2L, requestDto);
        itemDto.setRequestId(1L);

        ItemDto item = service.createItem(1L, itemDto);

        assertNotNull(item);
        assertEquals(1L, item.getRequestId());
    }

    @Test
    void getItemTest() {
        createItemTest();
        ItemDto item = service.getItem(1L, 1L);
        assertEquals(1L, item.getId());
        assertEquals("item", item.getName());
        assertEquals(1L, item.getOwner());
    }

    @Test
    void getAllItems() {
        createItemTest();
        List<ItemDto> items = service.getAllItems(1L, 0, 10);
        assertEquals(1, items.size());
    }

    @Test
    void searchItemTest() {
        createItemTest();
        List<ItemDto> items = service.searchItem(2L, "te", 0, 10);
        assertEquals(1, items.size());
    }

    @Test
    void searchItemNullMatchTest() {
        List<ItemDto> items = service.searchItem(1L, "", 0, 1);
        assertNotNull(items);
    }

    @Test
    void updateItemTest() {
        createItemTest();
        itemDto.setDescription("desc");
        itemDto.setName("updatedName");
        ItemDto item = service.updateItem(1L, 1L, itemDto);

        assertNotNull(item);
        assertEquals("updatedName", item.getName());
        assertEquals("desc", item.getDescription());
    }

    @Test
    void updateItemNoOwnerTest() {
        createItemTest();
        itemDto.setDescription("desc");
        itemDto.setName("updatedName");
        assertThrows(NotFoundException.class, () -> service.updateItem(2L, 1L, itemDto));
    }

    @Test
    void updateItemNotExistsTest() {
        assertThrows(NotFoundException.class, () -> service.updateItem(2L, 1L, itemDto));
    }

    @Test
    void addCommentTest() {
        UserDto user = UserDto.builder()
                .name("user2")
                .email("user@email.com")
                .build();
        createItemTest();

        user.setEmail("user2@email.com");
        userService.createUser(user);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusMinutes(1));

        bookingService.createBooking(2L, bookingDto);
        bookingService.approveBooking(1L, 1L, true);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("1111");

        CommentDto comment = service.addComment(2L, 1L, commentDto);

        assertNotNull(comment);
        assertNotNull(comment.getId());
        assertNotNull(comment.getCreated());
        assertEquals("1111", comment.getText());
        assertEquals("user2", comment.getAuthorName());

        ItemDto createItemDto = service.getItem(1L, 1L);
        assertNotNull(createItemDto);
        assertNotNull(createItemDto.getLastBooking());
        assertNull(createItemDto.getNextBooking());
    }

    @Test
    void addCommentNoBooking() {
        UserDto user = UserDto.builder()
                .name("user2")
                .email("user@email.com")
                .build();
        createItemTest();

        user.setEmail("user2@email.com");
        userService.createUser(user);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusMinutes(1));

        bookingService.createBooking(2L, bookingDto);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("1111");

        assertThrows(NotBookedException.class, () -> service.addComment(2L, 1L, commentDto));
    }

}
