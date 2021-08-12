package ro.rentea.lidldigitalinterviewapp.exception;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Order(4)
@ControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {

    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String globalExceptionHandler(RuntimeException exception) {
        if (exception.getMessage().contains("class ro.rentea.lidldigitialinterviewapp.entities.")) {
            return exception.getMessage().replace("class ro.rentea.lidldigitialinterviewapp.entities.", "");
        }

        return exception.getMessage();
    }

}
