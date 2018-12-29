package com.danken;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.danken.business.LabowletError;

/***
 * The global error handler for Labowlet. Some methods will handle multiple exceptions. Regardless, all methods will use the special LabowletError class to 
 * create a response. 
 * 
 */
@ControllerAdvice
public class LabowletExceptionHandler extends ResponseEntityExceptionHandler {

    /***
     * Illegal Arguments Handler
     * 
     */
    @ExceptionHandler(value = {IllegalArgumentException.class})
    protected ResponseEntity<Object> handleIllegalArgumentException(RuntimeException ex, WebRequest request){
        return buildResponseEntity(new LabowletError(HttpStatus.BAD_REQUEST, "Labowlet has encountered an Illegal Argument error. This generally occurs due to malformed inputs.", ex));
    }

    /***
     * Illegal State Handler 
     * 
     */
    @ExceptionHandler(value = {IllegalStateException.class})
    protected ResponseEntity<Object> handleIllegalStateException(RuntimeException ex, WebRequest request){
        return buildResponseEntity(new LabowletError(HttpStatus.BAD_REQUEST, "Labowlet has caught an Illegal State error. This generally occurs due to the request violating certain rules.", ex));
    }


    /***
     * 
     * This is the general fallback exception handler. This will generally capture all errors, and most of the time, if the exception went to this handler, it is 
     * an uncaught exception, unaccounted for in some form and therefore needs to be dealt with in maintenance.
     * 
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleRemainingExceptions(Exception ex, WebRequest request){
        return buildResponseEntity(new LabowletError(HttpStatus.INTERNAL_SERVER_ERROR, "Labowlet has encountered an unknown error. Please contact the creator with this issue.", ex));
    }

    /***
     * Builds a response entitiy with the body of a custom LabowletError
     * 
     * @param error Custom Labowlet Error Object that will be displayed onto the client
     * 
     */
    private ResponseEntity<Object> buildResponseEntity(LabowletError error){
        return new ResponseEntity<>(error, error.getStatus());
    }

    //todo uncaught exceptions

}