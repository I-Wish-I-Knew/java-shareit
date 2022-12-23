package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.booking.dto.GetAllBookingsRequest;
import ru.practicum.shareit.exception.ExchangeFilterFnc;

import java.util.List;

@Service
public class BookingClient {
    private final WebClient webClient;
    private static final String HEADER = "X-Sharer-User-Id";
    private static final String BOOKING_ID_PARAM = "/{bookingId}";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl) {
        this.webClient = WebClient.builder()
                .filter(ExchangeFilterFnc.errorHandler())
                .baseUrl(serverUrl + "/bookings")
                .build();
    }

    public ResponseEntity<BookingDtoInfo> save(BookingDto bookingDto, Long userId) {

        return webClient
                .post()
                .header(HEADER, String.valueOf(userId))
                .body(BodyInserters.fromValue(bookingDto))
                .retrieve()
                .toEntity(BookingDtoInfo.class)
                .block();
    }

    public ResponseEntity<BookingDtoInfo> updateStatusOwner(Long id, Boolean approved, Long ownerId) {

        return webClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(BOOKING_ID_PARAM)
                        .queryParam("approved", approved)
                        .build(id))
                .header(HEADER, String.valueOf(ownerId))
                .retrieve()
                .toEntity(BookingDtoInfo.class)
                .block();
    }

    public ResponseEntity<BookingDtoInfo> get(Long id, Long userId) {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(BOOKING_ID_PARAM)
                        .build(id))
                .header(HEADER, String.valueOf(userId))
                .retrieve()
                .toEntity(BookingDtoInfo.class)
                .block();
    }

    public ResponseEntity<List<BookingDtoInfo>> getAll(GetAllBookingsRequest request) {
        if (request.isOwner()) {
            return webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/owner")
                            .queryParam("state", request.getState().name())
                            .queryParam("from", request.getFrom())
                            .queryParam("size", request.getSize())
                            .build())
                    .header(HEADER, String.valueOf(request.getUserId()))
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<List<BookingDtoInfo>>() {
                    })
                    .block();
        }
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("state", request.getState().name())
                        .queryParam("from", request.getFrom())
                        .queryParam("size", request.getSize())
                        .build())
                .header(HEADER, String.valueOf(request.getUserId()))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<BookingDtoInfo>>() {
                })
                .block();
    }
}
