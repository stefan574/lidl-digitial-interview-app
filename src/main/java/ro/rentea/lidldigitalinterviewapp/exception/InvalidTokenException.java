package ro.rentea.lidldigitalinterviewapp.exception;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(int token) {
        super("Token " + token + " is invalid.");
    }

}
