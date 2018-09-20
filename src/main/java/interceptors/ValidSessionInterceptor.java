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
 * This interceptor is simply to verify the existence of a Room for the request that is it mapped to as well as to check if you're able to do requests onto this path.
 *
 */
public class ValidSessionInterceptor extends HandlerInterceptorAdapter {

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
            HttpSession session = request.getSession(false);
            LabowletState applicationState = LabowletState.getInstance();
            GameSession userSession = applicationState.getGameSession(session);
            if(userSession == null){
                JsonErrorResponseHandler.sendErrorResponse(response, HttpStatus.BAD_REQUEST,
                        new IllegalStateException("No valid session has been found! " +
                                "Please check your x-auth-token header and make sure it has a valid token."));
                return false;
            }

            return true;
    }
}