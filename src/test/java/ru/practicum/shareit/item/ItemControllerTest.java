package ru.practicum.shareit.item;

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
import ru.practicum.shareit.exception.UnavailableForUserException;
import ru.practicum.shareit.exception.UpdateFailedException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoInfo;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @MockBean
    private ItemService service;
    @Autowired
    private MockMvc mockMvc;
    private ItemDto itemDto;
    private ItemDtoInfo itemDtoInfo;
    private CommentDto commentDto;
    private CommentDtoInfo commentDtoInfo;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto(
                1L,
                "item",
                "description",
                true
        );
        itemDtoInfo = new ItemDtoInfo(
                1L,
                "item",
                "description",
                true,
                null,
                null,
                Collections.emptyList()
        );
        commentDto = new CommentDto(
                1L,
                "text"
        );
        commentDtoInfo = new CommentDtoInfo(
                1L,
                "text",
                "name",
                LocalDateTime.now().withNano(0)
        );
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(service);
    }

    @Test
    void getAllByUser() throws Exception {
        when(service.getAllByUser(anyLong(), anyInt(), anyInt())).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(service, times(1))
                .getAllByUser(1L, 1, 1);
    }

    @Test
    void get() throws Exception {
        when(service.get(anyLong(), anyLong())).thenReturn(itemDtoInfo);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoInfo.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoInfo.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoInfo.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoInfo.getAvailable())));

        verify(service, times(1))
                .get(1L, 1L);
    }

    @Test
    void save() throws Exception {
        when(service.save(any(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(service, times(1))
                .save(itemDto, 1L);
    }

    @Test
    void saveComment() throws Exception {
        when(service.saveComment(anyLong(), any(), anyLong())).thenReturn(commentDtoInfo);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoInfo.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDtoInfo.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDtoInfo.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDtoInfo.getCreated().toString())));

        verify(service, times(1))
                .saveComment(1L, commentDto, 1L);
    }

    @Test
    void saveCommentWithUnavailableForUser() throws Exception {
        when(service.saveComment(anyLong(), any(), anyLong()))
                .thenThrow(new UnavailableForUserException("User can't write a review for this item"));

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service, times(1))
                .saveComment(1L, commentDto, 1L);
    }

    @Test
    void update() throws Exception {
        when(service.update(anyString(), anyLong(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(service, times(1))
                .update(mapper.writeValueAsString(itemDto), 1L, 1L);
    }

    @Test
    void updateWithUpdateFailed() throws Exception {
        when(service.update(anyString(), anyLong(), anyLong()))
                .thenThrow(new UpdateFailedException("Не удалось обновить данные"));

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service, times(1))
                .update(mapper.writeValueAsString(itemDto), 1L, 1L);
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());

        verify(service, times(1))
                .delete(1L, 1L);
    }

    @Test
    void searchItem() throws Exception {
        when(service.searchItem(anyString(), anyLong(), anyInt(), anyInt())).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "1")
                        .param("size", "1")
                        .param("text", "text"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(service, times(1))
                .searchItem("text", 1L, 1, 1);
    }
}