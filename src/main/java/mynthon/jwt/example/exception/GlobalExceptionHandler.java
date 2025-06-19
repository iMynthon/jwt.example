package mynthon.jwt.example.exception;
import mynthon.jwt.example.web.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse catchEntityNotFoundException(EntityNotFoundException ene){
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(),ene.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DuplicateEmailException.class)
    public ErrorResponse cathDuplicateEmailException(DuplicateEmailException dee){
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), dee.getMessage());
    }
}
