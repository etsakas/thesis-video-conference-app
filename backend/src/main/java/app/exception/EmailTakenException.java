package app.exception;

public class EmailTakenException extends RuntimeException {

    public EmailTakenException() {
        super("EmailTakenException");
    }

    public EmailTakenException(String message) {
        super(message);
    }
}