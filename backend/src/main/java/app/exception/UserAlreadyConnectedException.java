package app.exception;

public class UserAlreadyConnectedException extends RuntimeException {
    public UserAlreadyConnectedException() {
        super("UserAlreadyConnectedException");
    }

    public UserAlreadyConnectedException(String message) {
        super(message);
    }
}
