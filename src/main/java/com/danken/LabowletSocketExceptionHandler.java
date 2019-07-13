package com.danken;

import com.danken.business.LabowletError;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class LabowletSocketExceptionHandler {


    @MessageExceptionHandler()
    @SendToUser("/client/errors")
    protected LabowletError handleIllegalStateException(Throwable ex) {
        return new LabowletError(HttpStatus.BAD_REQUEST, "Labowlet has caught an Illegal State error. This generally occurs due to the request violating certain rules.", ex);
    }


}
