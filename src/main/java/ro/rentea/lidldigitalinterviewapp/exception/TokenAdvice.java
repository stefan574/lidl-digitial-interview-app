package ro.rentea.lidldigitalinterviewapp.exception;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Order(2)
@ControllerAdvice
public class TokenAdvice {

    @ResponseBody
    @ExceptionHandler(IncorrectEmailAddressOrPasswordException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String incorrectEmailAddressOrPasswordHandler(IncorrectEmailAddressOrPasswordException exception) {
        return exception.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String invalidTokenHandler(InvalidTokenException exception) {
        return exception.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(TokenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String tokenNotFoundHandler(TokenNotFoundException exception) {
        return exception.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(TokenNotNumberException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String tokenNotNumberHandler(TokenNotNumberException exception) {
        return exception.getMessage();
    }

}
