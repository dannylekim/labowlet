package application;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import business.LabowletError;

@ControllerAdvice
public class LabowletExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {IllegalArgumentException.class, IllegalStateException.class})
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request){
        return buildResponseEntity(new LabowletError(HttpStatus.BAD_REQUEST, "Labowlet has encountered an error", ex));
    }

    private ResponseEntity<Object> buildResponseEntity(LabowletError error){
        return new ResponseEntity<>(error, error.getStatus());
    }

    //todo uncaught exceptions

}