package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.shareit.exception.ExchangeFilterFnc;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoInfo;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;

import java.util.List;

@Service
public class ItemClient {

    private final WebClient webClient;
    private static final String HEADER = "X-Sharer-User-Id";
    private static final String ITEM_ID_PARAM = "/{itemId}";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl) {
        this.webClient = WebClient.builder()
                .filter(ExchangeFilterFnc.errorHandler())
                .baseUrl(serverUrl + "/items")
                .build();
    }

    public ResponseEntity<ItemDto> save(ItemDto itemDto, Long userId) {

        return webClient
                .post()
                .header(HEADER, String.valueOf(userId))
                .body(BodyInserters.fromValue(itemDto))
                .retrieve()
                .toEntity(ItemDto.class)
                .block();
    }

    public ResponseEntity<ItemDto> update(String updatedFields, Long itemId, Long userId) {

        return webClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(ITEM_ID_PARAM)
                        .build(itemId))
                .header(HEADER, String.valueOf(userId))
                .body(BodyInserters.fromValue(updatedFields))
                .retrieve()
                .toEntity(ItemDto.class)
                .block();
    }

    public ResponseEntity<ItemDtoInfo> get(Long id, Long userId) {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ITEM_ID_PARAM)
                        .build(id))
                .header(HEADER, String.valueOf(userId))
                .retrieve()
                .toEntity(ItemDtoInfo.class)
                .block();
    }

    public ResponseEntity<List<ItemDtoInfo>> getAllByUser(Long userId, Integer from, Integer size) {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .build())
                .header(HEADER, String.valueOf(userId))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<ItemDtoInfo>>() {
                })
                .block();
    }

    public void delete(Long id, Long userId) {
        webClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(ITEM_ID_PARAM)
                        .build(id))
                .header(HEADER, String.valueOf(userId))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public ResponseEntity<List<ItemDto>> searchItem(String text, Long userId, Integer from, Integer size) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .queryParam("text", text)
                        .build())
                .header(HEADER, String.valueOf(userId))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<ItemDto>>() {
                })
                .block();
    }

    public ResponseEntity<CommentDtoInfo> saveComment(Long itemId, CommentDto commentDto, Long userId) {

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(ITEM_ID_PARAM + "/comment")
                        .build(itemId))
                .header(HEADER, String.valueOf(userId))
                .body(BodyInserters.fromValue(commentDto))
                .retrieve()
                .toEntity(CommentDtoInfo.class)
                .block();
    }
}
