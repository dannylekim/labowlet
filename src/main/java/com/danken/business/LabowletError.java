package com.danken.business;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

/***
 * Labowlet Error is the general error structure that is used for all api rest requests. It has status, a specific timestamp, message, debug message
 * and a list of subErrors which are used for errors that may have multiple errors such as validations.
 *
 */
@Getter
@Setter
@Slf4j
public class LabowletError {

    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private String message;
    private String debugMessage;
    private List<LabowletSubError> subErrors; //this is generally used during validations or other custom exceptions created that will allow subErrors to be available.

    private LabowletError() {
        timestamp = LocalDateTime.now();
    }

    public LabowletError(HttpStatus status) {
        this();
        this.status = status;
        log.info("Created error object with status {}", status.toString());
    }

    public LabowletError(HttpStatus status, Throwable ex) {


        this();
        this.status = status;
        this.message = "Labowlet received an unexpected error.";
        this.debugMessage = ex.getLocalizedMessage();

        log.info("Created error object with status {} with error debug message {}", status.toString(),
                this.debugMessage);
        log.debug("Stack trace", ex);
    }

    public LabowletError(HttpStatus status, String message, Throwable ex) {
        this();
        this.status = status;
        this.message = message;
        this.debugMessage = ex.getLocalizedMessage();
        log.info("Created error object with status {} with error message {} with debug message: {}", status.toString(),
                this.message, this.debugMessage);

        log.debug("Stack trace", ex);
    }

}