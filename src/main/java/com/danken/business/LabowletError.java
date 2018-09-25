package com.danken.business;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

/***
 * Labowlet Error is the general error structure that is used for all api rest requests. It has status, a specific timestamp, message, debug message
 * and a list of subErrors which are used for errors that may have multiple errors such as validations.
 *
 */
public class LabowletError {

    private static final Logger logger = LoggerFactory.getLogger(LabowletError.class);

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
        logger.info("Created error object with status {}", status.toString());
    }

    public LabowletError(HttpStatus status, Throwable ex) {


        this();
        this.status = status;
        this.message = "Labowlet received an unexpected error.";
        this.debugMessage = ex.getLocalizedMessage();

        logger.info("Created error object with status {} with error debug message {}", status.toString(),
                this.debugMessage);
        logger.debug("Stack trace", ex);
    }

    public LabowletError(HttpStatus status, String message, Throwable ex) {
        this();
        this.status = status;
        this.message = message;
        this.debugMessage = ex.getLocalizedMessage();
        logger.info("Created error object with status {} with error message {} with debug message: {}", status.toString(),
                this.message, this.debugMessage);

        logger.debug("Stack trace", ex);
    }

    // Getters & Setters

    /**
     * @return the status
     */
    public HttpStatus getStatus() {
        return status;
    }

    /**
     * @return the debugMessage
     */
    public String getDebugMessage() {
        return debugMessage;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    /**
     * @param debugMessage the debugMessage to set
     */
    public void setDebugMessage(String debugMessage) {
        this.debugMessage = debugMessage;
    }

    /**
     * @return the subErrors
     */
    public List<LabowletSubError> getSubErrors() {
        return subErrors;
    }

    /**
     * @param subErrors the subErrors to set
     */
    public void setSubErrors(List<LabowletSubError> subErrors) {
        this.subErrors = subErrors;
    }


}