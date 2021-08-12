package ro.rentea.lidldigitalinterviewapp.exception;

public class TokenNotFoundException extends RuntimeException {

    public TokenNotFoundException(String emailAddress) {
        super("Could not find token for email address " + emailAddress + ".");
    }

}
