package mynthon.jwt.example.jwt.example.exception;

public class TokenParseException extends RuntimeException {
    public TokenParseException(String message) {
        super(message);
    }
}
