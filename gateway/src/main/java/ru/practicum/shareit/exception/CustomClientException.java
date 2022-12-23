package ru.practicum.shareit.exception;

public class CustomClientException extends RuntimeException {

    public CustomClientException(String message) {
        super(message);
    }
}
