package app.resources;

public class MessageResource {
    private String message;

    public MessageResource(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
