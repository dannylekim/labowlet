package com.danken.utility;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.http.HttpStatus;

import com.danken.business.LabowletError;


/***
 *  Utility Class that deals with Error Responses and their Json Serialization
 * 
 */
public class JsonErrorResponseHandler {
    
    
    /**
     *  Prints out a custom Error response in according to Labowlet. There are no nested errors that can be created from this method.
     * 
     * @param response the HttpServletResponse used to print out a reply to client
     * @param errorStatus an HttpStatus that should correspond to an error HttpStatus 
     * @param ex The runtime Exception used 
     * @throws Exception 
     */
    public static void sendErrorResponse(HttpServletResponse response, HttpStatus errorStatus, Throwable ex) throws IOException{
        LabowletError error = new LabowletError(errorStatus, ex);
        String serializedError = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .writeValueAsString(error);
        response.setStatus(error.getStatus().value());
        response.getWriter().print(serializedError);
    }
}