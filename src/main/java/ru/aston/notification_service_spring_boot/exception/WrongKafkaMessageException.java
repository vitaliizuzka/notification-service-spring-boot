package ru.aston.notification_service_spring_boot.exception;

public class WrongKafkaMessageException extends RuntimeException{
    public WrongKafkaMessageException() {
    }

    public WrongKafkaMessageException(String message) {
        super(message);
    }

    public WrongKafkaMessageException(Throwable cause) {
        super(cause);
    }
}
