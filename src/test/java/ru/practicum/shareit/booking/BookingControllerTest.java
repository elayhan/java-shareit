package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadBookingTimeException;
import ru.practicum.shareit.exception.NotSupportState;
import ru.practicum.shareit.util.CustomHeader;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {
    public static final String URL_TEMPLATE = "/bookings";
    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingService service;

    private ObjectMapper mapper = new ObjectMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .setDateFormat(new StdDateFormat().withColonInTimeZone(true))
            .registerModule(new JavaTimeModule());

    @Test
    void createBookingNotValidTest() throws Exception {
        mvc.perform(post(URL_TEMPLATE)
                        .header(CustomHeader.USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new BookingDto())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBookingPastStartTest() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.of(2000, 1, 1, 0, 0));
        bookingDto.setEnd(LocalDateTime.of(2002, 1, 1, 0, 0));
        bookingDto.setItemId(1L);

        mvc.perform(post(URL_TEMPLATE)
                        .header(CustomHeader.USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBookingBadBookingTimeTest() throws Exception {
        when(service.createBooking(any(), any())).thenThrow(new BadBookingTimeException("Время бо-бо"));
        mvc.perform(post(URL_TEMPLATE)
                        .header(CustomHeader.USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(getBookingDto())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBookingEmptyItemTest() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusMinutes(1));
        bookingDto.setEnd(LocalDateTime.now().plusMinutes(2));

        mvc.perform(post(URL_TEMPLATE)
                        .header(CustomHeader.USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBookingTest() throws Exception {
        BookingDto bookingDto = getBookingDto();

        when(service.createBooking(any(), any())).thenReturn(bookingDto);

        mvc.perform(post(URL_TEMPLATE)
                        .header(CustomHeader.USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(0)));
    }

    @Test
    void approveBookingTest() throws Exception {
        BookingDto bookingDto = getBookingDto();
        bookingDto.setStatus(BookingStatus.APPROVED);
        when(service.approveBooking(any(), any(), any())).thenReturn(bookingDto);

        mvc.perform(patch(URL_TEMPLATE + "/{bookingId}", 1L)
                        .header(CustomHeader.USER_ID, 1L)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(0)))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.toString())));
    }

    @Test
    void approveBookingNullApproveTest() throws Exception {
        mvc.perform(patch(URL_TEMPLATE + "/{bookingId}", 1L)
                        .header(CustomHeader.USER_ID, 1L)
                        .param("approved", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingTest() throws Exception {
        BookingDto bookingDto = getBookingDto();

        when(service.getBooking(any(), any())).thenReturn(bookingDto);

        mvc.perform(get(URL_TEMPLATE + "/{bookingId}", 1L)
                        .header(CustomHeader.USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) bookingDto.getId())));
    }

    @Test
    void getBookingNoUserIdTest() throws Exception {
        mvc.perform(get(URL_TEMPLATE + "/{bookingId}", -9))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllBookingByStateTest() throws Exception {
        mvc.perform(get(URL_TEMPLATE)
                        .header(CustomHeader.USER_ID, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBookingByStateAllParamTest() throws Exception {
        mvc.perform(get(URL_TEMPLATE)
                        .header(CustomHeader.USER_ID, 1L)
                        .param("state", "APPROVED")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBookingByOwnerAndStateTest() throws Exception {
        mvc.perform(get(URL_TEMPLATE + "/owner")
                        .header(CustomHeader.USER_ID, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBookingByOwnerAndStateAllParamTest() throws Exception {
        mvc.perform(get(URL_TEMPLATE + "/owner")
                        .header(CustomHeader.USER_ID, 1L)
                        .param("state", "APPROVED")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBookingByOwnerAndStateUnknownTest() throws Exception {
        when(service.getAllBookingsByOwnedAndState(any(), any(), any(), any())).thenThrow(new NotSupportState("Не поддерживается"));
        mvc.perform(get(URL_TEMPLATE + "/owner")
                        .header(CustomHeader.USER_ID, 1L)
                        .param("state", "APPROVED")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().is5xxServerError());
    }


    private static BookingDto getBookingDto() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusMinutes(1));
        bookingDto.setEnd(LocalDateTime.now().plusMinutes(2));
        bookingDto.setItemId(1L);
        return bookingDto;
    }


}
