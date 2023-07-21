package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.util.CustomHeader;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemRequestService service;

    @Test
    void createRequestTest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("description");

        when(service.createRequest(any(), any())).thenReturn(itemRequestDto);
        mvc.perform(post("/requests")
                        .header(CustomHeader.USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("description", is(itemRequestDto.getDescription())));
    }

    @Test
    void getOwnerRequestsNotExistsUserTest() throws Exception {
        when(service.getOwnerRequests(any())).thenThrow(new NotFoundException("Пользователь не найден."));
        mvc.perform(get("/requests")
                        .header(CustomHeader.USER_ID, 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOwnerRequestTest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        when(service.getOwnerRequests(any())).thenReturn(List.of(itemRequestDto, itemRequestDto));

        mvc.perform(get("/requests")
                        .header(CustomHeader.USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getAllRequestTest() throws Exception {
        mvc.perform(get("/requests/all")
                        .header(CustomHeader.USER_ID, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getRequestByIdNotFoundTest() throws Exception {
        when(service.getRequestById(any(), any())).thenThrow(new NotFoundException(""));
        mvc.perform(get("/requests/{requestId}", 1)
                        .header(CustomHeader.USER_ID, 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRequestByIdTest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("sss");
        when(service.getRequestById(any(), any())).thenReturn(itemRequestDto);

        mvc.perform(get("/requests/{requestId}", 1)
                        .header(CustomHeader.USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId().intValue())))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }
}
