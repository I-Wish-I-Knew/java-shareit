package ru.practicum.shareit.exception;

public class UnchangeableStatusException extends RuntimeException {
    public UnchangeableStatusException(String message) {
        super(message);
    }
}
