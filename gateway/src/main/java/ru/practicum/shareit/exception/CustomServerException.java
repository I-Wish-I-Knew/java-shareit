package ru.practicum.shareit.exception;

public class CustomServerException extends RuntimeException {

    public CustomServerException(String message) {
        super(message);
    }
}
