package com.danken.interceptors.ValidSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.danken.interceptors.ValidSessionInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.danken.sessions.GameSession;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.io.PrintWriter;

public class PreHandleTests {

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    GameSession userSession;

    @Mock
    HttpSession session;

    @BeforeEach
    public void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);
        PrintWriter printer = mock(PrintWriter.class);
        doReturn(printer).when(response).getWriter();

    }


    @Test
    public void sessionIsValid() throws Exception{
        doReturn(session).when(request).getSession(false);
        doReturn(userSession).when(session).getAttribute(any());

        ValidSessionInterceptor interceptor = new ValidSessionInterceptor();

        assertTrue(interceptor.preHandle(request, response, null));
    }

    @Test
    public void sessionIsNotValid() throws Exception{
        doReturn(session).when(request).getSession(false);
        doReturn(null).when(session).getAttribute(any());

        ValidSessionInterceptor interceptor = new ValidSessionInterceptor();

        assertFalse(interceptor.preHandle(request, response, null));

    }



}