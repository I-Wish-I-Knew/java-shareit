package ru.practicum.shareit.exception;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

@UtilityClass
public class ExchangeFilterFnc {
    public static ExchangeFilterFunction errorHandler() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            HttpStatus status = clientResponse.statusCode();
            if (status.value() == 400) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new CustomClientException(errorBody)));
            } else if (status.value() == 404) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new NotFoundException(errorBody)));
            } else if (status.value() == 409) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new AlreadyExistsException(errorBody)));
            } else if (status.is5xxServerError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new CustomServerException(errorBody)));
            }
            return Mono.just(clientResponse);
        });
    }
}
