package ru.practicum.shareit.exception;

public class NotSupportState extends RuntimeException {
    public NotSupportState(String message) {
        super(message);
    }
}
