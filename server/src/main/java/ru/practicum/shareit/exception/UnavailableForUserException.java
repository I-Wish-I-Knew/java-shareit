package ru.practicum.shareit.exception;

public class UnavailableForUserException extends RuntimeException {
    public UnavailableForUserException(String message) {
        super(message);
    }
}
