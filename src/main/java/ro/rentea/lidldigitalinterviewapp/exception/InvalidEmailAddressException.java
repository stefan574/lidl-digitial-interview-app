package ro.rentea.lidldigitalinterviewapp.exception;

public class InvalidEmailAddressException extends RuntimeException {

    public InvalidEmailAddressException(String emailAddress) {
        super("Email address " + emailAddress + " is invalid.");
    }

}
