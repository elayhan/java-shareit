package ru.practicum.shareit.exception;

public class BadBookingTimeException extends RuntimeException {
    public BadBookingTimeException(String message) {
        super(message);
    }
}
