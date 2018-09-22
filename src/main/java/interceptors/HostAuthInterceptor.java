package interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.Host;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import application.LabowletState;
import business.Player;
import business.Room;
import sessions.GameSession;
import utility.JsonErrorResponseHandler;

/***
 *
 * This interceptor is meant to authorize hosts for certain room calls. It will check if the player in the session is
 * the host of the room for whatever paths it is configured to.
 *
 */
public class HostAuthInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(HostAuthInterceptor.class);
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object Handler, Exception exception) throws Exception{}
    
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
        HttpSession session = request.getSession(false);
        GameSession userSession = LabowletState.getInstance().getGameSession(session);
        Room currentRoom = userSession.getCurrentRoom();
        Player host = currentRoom.getHost();
        Player currentPlayer = userSession.getPlayer();
        if(!host.equals(currentPlayer)){
            logger.info(currentPlayer.getName() + " with ID " + currentPlayer.getId() + " is trying to access a host" +
                    "only request without host rights to the room " + currentRoom.getRoomCode());
            JsonErrorResponseHandler.sendErrorResponse(response, HttpStatus.FORBIDDEN, new IllegalAccessError("You are not authorized to perform this request!"));
            return false;
        }
        return true;
    }

}