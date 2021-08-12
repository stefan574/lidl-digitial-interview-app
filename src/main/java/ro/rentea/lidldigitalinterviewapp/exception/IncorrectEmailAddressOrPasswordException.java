package ro.rentea.lidldigitalinterviewapp.exception;

public class IncorrectEmailAddressOrPasswordException extends RuntimeException {

    public IncorrectEmailAddressOrPasswordException() {
        super("Email address or password are incorrect.");
    }

}
