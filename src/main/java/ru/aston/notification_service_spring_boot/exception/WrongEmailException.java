package ru.aston.notification_service_spring_boot.exception;

public class WrongEmailException extends RuntimeException{
    public WrongEmailException() {
    }

    public WrongEmailException(String message) {
        super(message);
    }

    public WrongEmailException(Throwable cause) {
        super(cause);
    }
}
