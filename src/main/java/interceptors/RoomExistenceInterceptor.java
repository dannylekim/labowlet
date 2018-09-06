package interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import application.LabowletState;
import business.Room;
import sessions.GameSession;
import utility.JsonErrorResponseHandler;

/***
 *
 * This interceptor is simply to verify the existence of a Room for the request that is it mapped to.
 * 
 */
public class RoomExistenceInterceptor extends HandlerInterceptorAdapter {
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object Handler, Exception exception) throws Exception{}
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView model9AndView)
    throws Exception {
      
    }

    /***
     * 
     * Handles the request that there is a room associated to this request.
     * 
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        if(!request.getMethod().equals("POST") && requestUri.contains("room") || requestUri.contains("team")) {
            HttpSession session = request.getSession(false);
            LabowletState applicationState = LabowletState.getInstance();
            GameSession userSession = applicationState.getGameSession(session);
            Room currentRoom = userSession.getCurrentRoom();
            
            //There needs to be a room associated to the session to be able to call these methods
            if(currentRoom == null) {
                JsonErrorResponseHandler.sendErrorResponse(response, HttpStatus.BAD_REQUEST, new IllegalStateException("You cannot perform this request because you haven't joined or created a room yet!"));
                return false;
            }

            //Check if the room is active in the application state
            boolean isRoomActive = (applicationState.getRoom(currentRoom.getRoomCode()) != null);
            if(!isRoomActive){
                JsonErrorResponseHandler.sendErrorResponse(response, HttpStatus.BAD_REQUEST, new IllegalStateException("You cannot perform this request because the room you are in is no longer active!"));
                return false;
            }


        }
      
       
        return true;
    }

}