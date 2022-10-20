package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.booking.dto.GetAllBookingsRequest;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.UnavailableItemException;
import ru.practicum.shareit.exception.UnchangeableStatusException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    private BookingService service;
    @Autowired
    private MockMvc mockMvc;
    private BookingDto bookingDto;
    private BookingDtoInfo bookingDtoInfo;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        bookingDto = new BookingDto(
                1L,
                1L,
                LocalDateTime.now().plusMonths(1).withNano(0),
                LocalDateTime.now().plusMonths(2).withNano(0),
                BookingStatus.WAITING
        );
        bookingDtoInfo = new BookingDtoInfo(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getStatus(),
                new BookingDtoInfo.Item(1L, "itemName"),
                new BookingDtoInfo.User(1L)
        );
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(service);
    }

    @Test
    void save() throws Exception {
        when(service.save(any(), any())).thenReturn(bookingDtoInfo);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoInfo.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingDtoInfo.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDtoInfo.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoInfo.getBooker().getId()), Long.class));

        verify(service, times(1))
                .save(bookingDto, 1L);
    }

    @Test
    void saveWithWrongDates() throws Exception {
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().minusDays(1));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        bookingDto.setStart(LocalDateTime.now().plusMonths(1).withNano(0));
        bookingDto.setEnd(LocalDateTime.now().plusMonths(2).withNano(0));

        verify(service, times(0))
                .save(bookingDto, 1L);
    }

    @Test
    void saveWithUnavailableItem() throws Exception {
        when(service.save(any(), any())).thenThrow(new UnavailableItemException("Item is booked"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service, times(1))
                .save(bookingDto, 1L);
    }

    @Test
    void updateStatusOwner() throws Exception {
        when(service.updateStatusOwner(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(bookingDtoInfo);

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoInfo.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingDtoInfo.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDtoInfo.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoInfo.getBooker().getId()), Long.class));

        verify(service, times(1))
                .updateStatusOwner(bookingDtoInfo.getId(), true, 1L);
    }

    @Test
    void updateUnchangeableStatus() throws Exception {
        when(service.updateStatusOwner(anyLong(), anyBoolean(), anyLong()))
                .thenThrow(new UnchangeableStatusException("Status can't be changed"));

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());

        verify(service, times(1))
                .updateStatusOwner(bookingDtoInfo.getId(), true, 1L);
    }

    @Test
    void get() throws Exception {
        when(service.get(anyLong(), anyLong())).thenReturn(bookingDtoInfo);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoInfo.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingDtoInfo.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDtoInfo.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoInfo.getBooker().getId()), Long.class));

        verify(service, times(1))
                .get(1L, 1L);
    }

    @Test
    void getAllByBookerAndState() throws Exception {
        when(service.getAll(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(service, times(1))
                .getAll(GetAllBookingsRequest.of(State.ALL, 1L,
                        false, 1, 1));
    }

    @Test
    void getAllByBookerAndUnsupportedState() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "UNSUPPORTED_STATUS")
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isInternalServerError());

        verify(service, times(0))
                .getAll(any());
    }

    @Test
    void getAllByOwnerAndState() throws Exception {
        when(service.getAll(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(service, times(1))
                .getAll(GetAllBookingsRequest.of(State.ALL, 1L,
                        true, 1, 1));
    }
}