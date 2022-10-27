package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInfo;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @MockBean
    private ItemRequestService service;
    @Autowired
    private MockMvc mockMvc;
    private ItemRequestDtoInfo requestDtoInfo;
    private ItemRequestDto requestDto;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        requestDtoInfo = new ItemRequestDtoInfo(
                1L,
                "description",
                LocalDateTime.now().withNano(0),
                new ArrayList<>()
        );
        requestDto = new ItemRequestDto(
                1L,
                "description",
                LocalDateTime.now().withNano(0)
        );
    }

    @Test
    void getOwn() throws Exception {
        when(service.getOwn(anyLong(), anyInt(), anyInt())).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "10")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(service, times(1))
                .getOwn(1L, 1, 10);
    }

    @Test
    void getAllPageable() throws Exception {
        when(service.getAllOtherUser(anyLong(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "10")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(service, times(1))
                .getAllOtherUser(1L, 1, 10);
    }

    @Test
    void get() throws Exception {
        when(service.get(anyLong(), anyLong())).thenReturn(requestDtoInfo);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDtoInfo.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDtoInfo.getDescription())))
                .andExpect(jsonPath("$.created", is(requestDtoInfo.getCreated().toString())))
                .andExpect(jsonPath("$.items", is(requestDtoInfo.getItems())));

        when(service.get(anyLong(), anyLong())).thenThrow(new NotFoundException("Запрос не найден"));
        mockMvc.perform(MockMvcRequestBuilders.get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound());

        verify(service, times(2))
                .get(1L, 1L);
    }

    @Test
    void save() throws Exception {
        when(service.save(any(), anyLong())).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(requestDto.getCreated().toString())));

        verify(service, times(1))
                .save(requestDto, 1L);
    }
}