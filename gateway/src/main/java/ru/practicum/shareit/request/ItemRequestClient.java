package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.shareit.exception.ExchangeFilterFnc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInfo;

import java.util.List;

@Service
public class ItemRequestClient {

    private final WebClient webClient;
    private static final String HEADER = "X-Sharer-User-Id";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl) {
        this.webClient = WebClient.builder()
                .filter(ExchangeFilterFnc.errorHandler())
                .baseUrl(serverUrl + "/requests")
                .build();
    }

    public ResponseEntity<List<ItemRequestDtoInfo>> getOwn(Long userId, int from, int size) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .build())
                .header(HEADER, String.valueOf(userId))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<ItemRequestDtoInfo>>() {
                })
                .block();
    }

    public ResponseEntity<List<ItemRequestDtoInfo>> getAllOtherUser(Long userId, int from, int size) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/all")
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .build())
                .header(HEADER, String.valueOf(userId))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<ItemRequestDtoInfo>>() {
                })
                .block();
    }

    public ResponseEntity<ItemRequestDtoInfo> get(Long requestId, Long userId) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{requestId}")
                        .build(requestId))
                .header(HEADER, String.valueOf(userId))
                .retrieve()
                .toEntity(ItemRequestDtoInfo.class)
                .block();
    }

    public ResponseEntity<ItemRequestDto> save(ItemRequestDto itemRequestDto, Long authorId) {
        return webClient
                .post()
                .header(HEADER, String.valueOf(authorId))
                .body(BodyInserters.fromValue(itemRequestDto))
                .retrieve()
                .toEntity(ItemRequestDto.class)
                .block();
    }
}
