package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleValidation(AlreadyExistsException e) {
        log.info(e.getMessage(), e);
        return new ErrorResponse("AlreadyExistsException", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(NotFoundException e) {
        log.info(e.getMessage(), e);
        return new ErrorResponse("NotFoundException", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUpdateFailed(UpdateFailedException e) {
        log.info(e.getMessage(), e);
        return new ErrorResponse("UpdateFailedException", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnavailableItem(UnavailableItemException e) {
        log.info(e.getMessage(), e);
        return new ErrorResponse("UnavailableItemException", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnchangeableStatus(UnchangeableStatusException e) {
        log.info(e.getMessage(), e);
        return new ErrorResponse("UnchangeableStatusException", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnavailableForUser(UnavailableForUserException e) {
        log.info(e.getMessage(), e);
        return new ErrorResponse("UnavailableForUserException", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpectedError(Throwable e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Unknown error", e.getMessage());
    }
}