package app.exception;

public class TokenException extends RuntimeException {
    public TokenException() {
        super("TokenException");
    }

    public TokenException(String message) {
        super(message);
    }
}
