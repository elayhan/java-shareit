package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    public static final String URL_TEMPLATE = "/users";
    @Mock
    private UserService service;

    @InjectMocks
    private UserController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("John")
                .email("john.doe@mail.com")
                .build();
    }

    @Test
    void createUserTest() throws Exception {
        when(service.createUser(any()))
                .thenReturn(userDto);

        mvc.perform(getAccept())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("name", is(userDto.getName())))
                .andExpect(jsonPath("email", is(userDto.getEmail())));
    }

    @Test
    void getUserByIdTest() throws Exception {
        when(service.getUser(any()))
                .thenReturn(userDto);

        mvc.perform(get(URL_TEMPLATE + "/" + 1))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getAllUsersEmptyTest() throws Exception {
        when(service.getAllUsers()).thenReturn(new ArrayList<>());

        mvc.perform(get(URL_TEMPLATE))
                .andExpect(jsonPath("$", hasSize(0)));

    }

    @Test
    void getAllUsersTwoUserTest() throws Exception {
        when(service.getAllUsers()).thenReturn(List.of(userDto, userDto));

        mvc.perform(get(URL_TEMPLATE))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void updateUserTest() throws Exception {
        when(service.updateUser(any(), any()))
                .thenReturn(userDto);

        mvc.perform(patch(URL_TEMPLATE + "/" + 1)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class));
    }

    @Test
    void deleteUserTest() throws Exception {
        mvc.perform(delete(URL_TEMPLATE + "/" + 1))
                .andExpect(status().isOk());
    }

    private MockHttpServletRequestBuilder getAccept() throws JsonProcessingException {
        return post(URL_TEMPLATE)
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

}
