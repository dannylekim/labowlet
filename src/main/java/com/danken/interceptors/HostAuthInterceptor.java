package com.danken.interceptors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.danken.business.Player;
import com.danken.business.Room;
import com.danken.sessions.GameSession;
import com.danken.utility.JsonErrorResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/***
 *
 * This interceptor is meant to authorize hosts for certain room calls. It will check if the player in the session is
 * the host of the room for whatever paths it is configured to.
 *
 */

@Component
public class HostAuthInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(HostAuthInterceptor.class);

    private GameSession userSession;

    @Inject
    public HostAuthInterceptor(GameSession userGameSession) {
        this.userSession = userGameSession;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object Handler, Exception exception) throws Exception {}

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    /***
     *
     * Handles the request before the handler to check if the player is the host.
     *
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Room currentRoom = userSession.getCurrentRoom();
        Player host = currentRoom.getHost();
        Player currentPlayer = userSession.getPlayer();
        if (!host.equals(currentPlayer)) {
            logger.info("{} with ID {} is trying to access a host {} only request without host rights to the room.", currentPlayer.getName(), currentPlayer.getId(), currentRoom.getRoomCode());
            JsonErrorResponseHandler.sendErrorResponse(response, HttpStatus.FORBIDDEN, new IllegalAccessError("You are not authorized to perform this request!"));
            return false;
        }
        return true;
    }

}