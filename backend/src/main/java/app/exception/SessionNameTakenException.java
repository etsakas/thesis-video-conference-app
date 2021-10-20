package app.exception;

public class SessionNameTakenException extends RuntimeException {

    public SessionNameTakenException() {
        super("SessionNameTakenException");
    }

    public SessionNameTakenException(String message) {
        super(message);
    }
}
