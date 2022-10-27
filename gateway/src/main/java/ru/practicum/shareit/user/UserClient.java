package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.shareit.exception.ExchangeFilterFnc;

import java.util.List;

@Service
public class UserClient {

    private final WebClient webClient;
    private static final String USER_ID_PARAM = "/{userId}";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl) {
        this.webClient = WebClient.builder()
                .filter(ExchangeFilterFnc.errorHandler())
                .baseUrl(serverUrl + "/users")
                .build();
    }

    public ResponseEntity<List<UserDto>> getAll() {
        return webClient
                .get()
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<UserDto>>() {
                })
                .block();
    }

    public ResponseEntity<UserDto> get(Long userId) {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(USER_ID_PARAM)
                        .build(userId))
                .retrieve()
                .toEntity(UserDto.class)
                .block();
    }

    public void delete(Long userId) {
        webClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(USER_ID_PARAM)
                        .build(userId))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public ResponseEntity<UserDto> save(UserDto userDto) {
        return webClient
                .post()
                .body(BodyInserters.fromValue(userDto))
                .retrieve()
                .toEntity(UserDto.class)
                .block();
    }

    public ResponseEntity<UserDto> update(Long userId, String updatedFields) {
        return webClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(USER_ID_PARAM)
                        .build(userId))
                .body(BodyInserters.fromValue(updatedFields))
                .retrieve()
                .toEntity(UserDto.class)
                .block();
    }
}
