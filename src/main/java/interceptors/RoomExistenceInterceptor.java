package interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import application.LabowletState;
import business.Room;
import sessions.GameSession;
import utility.JsonErrorResponseHandler;

/***
 *
 * This interceptor is simply to verify the existence of a Room for the request that is it mapped to as well as to check if you're able to do requests onto this path.
 * 
 */
public class RoomExistenceInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(RoomExistenceInterceptor.class);
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object Handler, Exception exception) throws Exception{}
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView){
      
    }

    /***
     * 
     * Handles the request that there is a room associated to this request.
     * 
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestMethod = request.getMethod();
        //In cors related requests, it will send an OPTIONS request first, we have to let that request go through.
        if( !requestMethod.equals("OPTIONS")) {
            HttpSession session = request.getSession(false);
            LabowletState applicationState = LabowletState.getInstance();
            GameSession userSession = applicationState.getGameSession(session);
            Room currentRoom = userSession.getCurrentRoom();
            
            //There needs to be a room associated to the session to be able to call these methods
            if(currentRoom == null) {
                logger.info(session.getId() + " is trying to access a room call that the session has no joined yet");
                JsonErrorResponseHandler.sendErrorResponse(response, HttpStatus.FAILED_DEPENDENCY, new IllegalStateException("You cannot perform this request because you haven't joined or created a room yet!"));
                return false;
            }

            //Check if the room is active in the application state
            boolean isRoomActive = (applicationState.getRoom(currentRoom.getRoomCode()) != null);
            if(!isRoomActive){
                logger.info(session.getId() + " is trying to access a room call where the room is no longer active");
                JsonErrorResponseHandler.sendErrorResponse(response, HttpStatus.GONE, new IllegalStateException("You cannot perform this request because the room you are in is no longer active!"));
                return false;
            }

            //Check if the room is in play or locked
            if(currentRoom.isInPlay() || currentRoom.isLocked()) {
                logger.info(session.getId() + " is trying to access a room call where the room has already started or is locked.");
                JsonErrorResponseHandler.sendErrorResponse(response, HttpStatus.CONFLICT, new IllegalStateException("Cannot execute this request as the game has already started for this room."));
                return false;
            }

        }
      
       
        return true;
    }

}