package com.danken.interceptors.HostAuth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.danken.business.Player;
import com.danken.business.Room;
import com.danken.interceptors.HostAuthInterceptor;
import com.danken.sessions.GameSession;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.io.PrintWriter;

public class PreHandleTests { 

    HttpServletRequest request;
    HttpServletResponse response;
    GameSession userSession;
    HttpSession session;

    @BeforeEach
    public void setUp() throws Exception{
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        userSession = mock(GameSession.class);
        session = mock(HttpSession.class);
        doReturn(session).when(request).getSession(false);
        doReturn(userSession).when(session).getAttribute(any());
        PrintWriter printer = mock(PrintWriter.class);
        doReturn(printer).when(response).getWriter();

    }


    @Test
    public void hostIsPlayer() throws Exception{
        Player host = mock(Player.class);
        Room room = mock(Room.class);
        doReturn(host).when(room).getHost();
        doReturn(room).when(userSession).getCurrentRoom();
        doReturn(host).when(userSession).getPlayer();

        HostAuthInterceptor interceptor = new HostAuthInterceptor();
        
        assertTrue(interceptor.preHandle(request, response, null));
    }

    @Test 
    public void hostIsNotPlayer() throws Exception{
        Player host = mock(Player.class);
        Room room = mock(Room.class);
        Player playerNotInside = mock(Player.class);
        doReturn(host).when(room).getHost();
        doReturn(room).when(userSession).getCurrentRoom();
        doReturn(playerNotInside).when(userSession).getPlayer();

        HostAuthInterceptor interceptor = new HostAuthInterceptor();
        
        assertFalse(interceptor.preHandle(request, response, null));
    }



}

