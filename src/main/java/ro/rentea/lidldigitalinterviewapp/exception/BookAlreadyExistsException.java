package ro.rentea.lidldigitalinterviewapp.exception;

public class BookAlreadyExistsException extends RuntimeException {

    public BookAlreadyExistsException(Long id) {
        super("Book with id " + id + " is already present.");
    }

}
