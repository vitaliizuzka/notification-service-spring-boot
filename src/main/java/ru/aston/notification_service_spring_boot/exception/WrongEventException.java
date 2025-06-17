package ru.aston.notification_service_spring_boot.exception;

public class WrongEventException extends RuntimeException{
    public WrongEventException() {
    }

    public WrongEventException(String message) {
        super(message);
    }

    public WrongEventException(Throwable cause) {
        super(cause);
    }
}
