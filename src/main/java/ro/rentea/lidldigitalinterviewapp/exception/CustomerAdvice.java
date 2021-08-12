package ro.rentea.lidldigitalinterviewapp.exception;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Order(1)
@ControllerAdvice
public class CustomerAdvice {

    @ResponseBody
    @ExceptionHandler(CustomerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String customerNotFoundHandler(CustomerNotFoundException exception) {
        return exception.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(InvalidEmailAddressException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String emailAddressIsInvalidHandler(InvalidEmailAddressException exception) {
        return exception.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(CustomerAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String customerAlreadyExistsHandler(CustomerAlreadyExistsException exception) {
        return exception.getMessage();
    }

}
