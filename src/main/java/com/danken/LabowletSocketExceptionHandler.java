package com.danken;

import com.danken.application.config.MessageSocketSender;
import com.danken.business.LabowletError;
import com.danken.utility.SocketSessionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class LabowletSocketExceptionHandler {

    private final MessageSocketSender sender;

    public LabowletSocketExceptionHandler(final MessageSocketSender sender) {
        this.sender = sender;
    }


    @MessageExceptionHandler
    protected void handleIllegalStateException(Throwable ex, SimpMessageHeaderAccessor simpMessageHeaderAccessor) {
        final var session = SocketSessionUtils.getSession(simpMessageHeaderAccessor);
        final var labowletError = new LabowletError(HttpStatus.BAD_REQUEST, "Labowlet has caught an Illegal State error. This generally occurs due to the request violating certain rules.", ex);
        sender.sendErrorMessage(session.getPlayer().getId(), labowletError);
    }


}
