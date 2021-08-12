package ro.rentea.lidldigitalinterviewapp.exception;

public class TokenNotNumberException extends RuntimeException {

    public TokenNotNumberException(String parameter) {
        super("Token must be a number but found a string instead: " + parameter + ".");
    }

}
