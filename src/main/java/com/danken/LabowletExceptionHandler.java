package com.danken;

import com.danken.business.LabowletError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/***
 * The global error handler for Labowlet. Some methods will handle multiple exceptions. Regardless, all methods will use the special LabowletError class to
 * create a response.
 *
 */
@RestControllerAdvice
public class LabowletExceptionHandler extends ResponseEntityExceptionHandler {

    /***
     * Illegal Arguments Handler
     *
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public LabowletError handleIllegalArgumentException(IllegalArgumentException ex) {
        return new LabowletError(HttpStatus.BAD_REQUEST, "Labowlet has encountered an Illegal Argument error. This generally occurs due to malformed inputs.", ex);
    }

    /***
     * Illegal State Handler
     *
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public LabowletError handleIllegalStateException(IllegalArgumentException ex) {
        return new LabowletError(HttpStatus.BAD_REQUEST, "Labowlet has caught an Illegal State error. This generally occurs due to the request violating certain rules.", ex);
    }


    /***
     *
     * This is the general fallback exception handler. This will generally capture all errors, and most of the time, if the exception went to this handler, it is
     * an uncaught exception, unaccounted for in some form and therefore needs to be dealt with in maintenance.
     *
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public LabowletError handleRemainingExceptions(Exception ex) {
        return new LabowletError(HttpStatus.INTERNAL_SERVER_ERROR, "Labowlet has encountered an unknown error. Please contact the creator with this issue.", ex);
    }


}