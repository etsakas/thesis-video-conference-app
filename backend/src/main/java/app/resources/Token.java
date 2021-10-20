package app.resources;

public class Token {
    String token;

    String expiresIn;

    private Token() {};
    
    public Token(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getExpiresIn() {
        return this.expiresIn;
    }

}
