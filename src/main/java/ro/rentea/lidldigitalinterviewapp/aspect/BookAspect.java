package ro.rentea.lidldigitalinterviewapp.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.rentea.lidldigitalinterviewapp.service.TokenService;

@Aspect
@Component
public class BookAspect {

    @Autowired
    private TokenService tokenService;

    @Pointcut("execution(* getAllBooks(int))")
    private void getAllBooks() {
    }

    @Pointcut("execution(* getBookById(Long,int))")
    private void getBookById() {
    }

    @Pointcut("execution(* createBook(ro.rentea.lidldigitalinterviewapp.entity.Book,int))")
    private void createBook() {
    }

    @Pointcut("execution(* updateBook(Long,ro.rentea.lidldigitalinterviewapp.entity.Book,int))")
    private void updateBook() {
    }

    @Pointcut("execution(* deleteBook(Long,int))")
    private void deleteBook() {
    }

    @Before("getAllBooks() || getBookById() || createBook() || updateBook() || deleteBook()")
    private void verifyToken(JoinPoint joinPoint) {
        Object[] parameters = joinPoint.getArgs();
        tokenService.doesTokenExist((int) parameters[parameters.length - 1]);
    }

}
