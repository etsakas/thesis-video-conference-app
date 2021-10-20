package app.exception;

public class DeletedAccountException extends RuntimeException {

    public DeletedAccountException() {
        super("DeletedAccountException");
    }

    public DeletedAccountException(String message) {
        super(message);
    }
}