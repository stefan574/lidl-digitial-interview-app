package ro.rentea.lidldigitalinterviewapp.exception;

public class CustomerAlreadyExistsException extends RuntimeException {

    public CustomerAlreadyExistsException(Long id) {
        super("Customer with id " + id + " is already present.");
    }

}
