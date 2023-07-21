package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.CustomHeader;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {
    public static final String URL_TEMPLATE = "/items";
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemService service;

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("name")
            .description("description")
            .owner(1L)
            .available(true)
            .build();

    @Test
    void createItemTest() throws Exception {
        when(service.createItem(any(), any())).thenReturn(itemDto);

        mvc.perform(post(URL_TEMPLATE)
                        .header(CustomHeader.USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId().intValue())));
    }

    @Test
    void createItemInvalidUserTest() throws Exception {
        when(service.createItem(any(), any())).thenThrow(new NotFoundException("Пользователь не найден"));
        mvc.perform(post(URL_TEMPLATE)
                        .header(CustomHeader.USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(itemDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateItemTest() throws Exception {
        when(service.updateItem(any(), any(), any())).thenReturn(itemDto);
        mvc.perform(patch("/items/{itemId}", 1L)
                        .header(CustomHeader.USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId().intValue())));
    }

    @Test
    void updateItemNotExists() throws Exception {
        when(service.updateItem(any(), any(), any())).thenThrow(new NotFoundException(""));
        mvc.perform(patch("/items/{itemId}", 1L)
                        .header(CustomHeader.USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(itemDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getItemByIdTest() throws Exception {
        when(service.getItem(any(), any())).thenReturn(itemDto);
        mvc.perform(get("/items/{itemId}", 1L)
                        .header(CustomHeader.USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId().intValue())));
    }

    @Test
    void getItemByIdNotFoundTest() throws Exception {
        when(service.getItem(any(), any())).thenThrow(new NotFoundException(""));
        mvc.perform(get("/items/{itemId}", 1L)
                        .header(CustomHeader.USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllItemsTest() throws Exception {
        when(service.getAllItems(any(), any(), any())).thenReturn(List.of(itemDto, itemDto));
        mvc.perform(get(URL_TEMPLATE)
                        .header(CustomHeader.USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getAllItemsInvalidUserTest() throws Exception {
        when(service.getAllItems(any(), any(), any())).thenThrow(new NotFoundException(""));
        mvc.perform(get(URL_TEMPLATE)
                        .header(CustomHeader.USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchItemTest() throws Exception {
        when(service.searchItem(any(), any(), any(), any())).thenReturn(List.of(itemDto));
        mvc.perform(get("/items/search")
                        .header(CustomHeader.USER_ID, 1L)
                        .param("text", "1")
                        .param("from", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void addCommentTest() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("1");
        commentDto.setItemId(1L);
        commentDto.setId(1L);

        when(service.addComment(any(), any(), any())).thenReturn(commentDto);
        mvc.perform(post("/items/{id}/comment", 1)
                        .header(CustomHeader.USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId().intValue())));
    }

}
