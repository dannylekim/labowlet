package com.danken.interceptors.RoomExistence;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.danken.application.LabowletState;
import com.danken.business.room.Room;
import com.danken.interceptors.RoomExistenceInterceptor;
import com.danken.sessions.GameSession;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.io.PrintWriter;

public class PreHandleTests { 

    HttpServletRequest request;
    HttpServletResponse response;
    GameSession userSession;
    LabowletState state;
    HttpSession session;

    @BeforeEach
    public void setUp() throws Exception{
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        userSession = mock(GameSession.class);
        session = mock(HttpSession.class);
        state = LabowletState.getInstance();
        doReturn(session).when(request).getSession(false);
        doReturn(userSession).when(session).getAttribute(any());

        PrintWriter printer = mock(PrintWriter.class);
        doReturn(printer).when(response).getWriter();

    }


    @Test
    public void roomIsNull() throws Exception{
        Room mockRoom = mock(Room.class);
        doReturn(mockRoom).when(userSession).getCurrentRoom();
        doReturn("PUT").when(request).getMethod();
        doReturn("room").when(request).getRequestURI();
        doReturn("test").when(mockRoom).getRoomCode();
        state.addActiveRoom(mockRoom);

        RoomExistenceInterceptor interceptor = new RoomExistenceInterceptor();
        
        assertTrue(interceptor.preHandle(request, response, null));
    }

    @Test 
    public void RoomIsNotNull() throws Exception{
        doReturn(null).when(userSession).getCurrentRoom();
        doReturn("PUT").when(request).getMethod();
        doReturn("room").when(request).getRequestURI();

        RoomExistenceInterceptor interceptor = new RoomExistenceInterceptor();
        
        assertFalse(interceptor.preHandle(request, response, null));
    }


    @Test 
    public void pathHasTeam() throws Exception{
        doReturn(null).when(userSession).getCurrentRoom();
        doReturn("TEST").when(request).getMethod();
        doReturn("team").when(request).getRequestURI();

        RoomExistenceInterceptor interceptor = new RoomExistenceInterceptor();
        
        assertFalse(interceptor.preHandle(request, response, null));
    }


    @Test
    public void notActiveRoom() throws Exception {
        Room room = mock(Room.class);
        doReturn(room).when(userSession).getCurrentRoom();
        doReturn("PUT").when(request).getMethod();
        doReturn("room").when(request).getRequestURI();
        RoomExistenceInterceptor interceptor = new RoomExistenceInterceptor();
        
        assertFalse(interceptor.preHandle(request, response, null));


    
    } 




}

