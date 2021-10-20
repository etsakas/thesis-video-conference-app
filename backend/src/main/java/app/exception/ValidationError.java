package app.exception;

public class ValidationError {
    private String field;
    private String message;
    private String rejectedValue;

    ValidationError(String field, String message, String rejectedValue) {
        this.field = field;
        this.message = message;
        this.rejectedValue = rejectedValue;
    }

    public String getField() {
        return this.field;
    }

    public String getMessage() {
        return this.message;
    }
    
    public String getRejectedValue() {
        return this.rejectedValue;
    }
}