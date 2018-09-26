package com.danken.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.danken.application.LabowletState;
import com.danken.sessions.GameSession;
import com.danken.utility.JsonErrorResponseHandler;

/***
 *
 * This interceptor is simply to verify the existence of a Room for the request that is it mapped to as well as to check if you're able to do requests onto this path.
 *
 */
@Slf4j
public class ValidSessionInterceptor extends HandlerInterceptorAdapter {

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object Handler, Exception exception) throws Exception{}

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    /***
     *
     * Handles the request that there is a room associated to this request.
     *
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!request.getMethod().equalsIgnoreCase("OPTIONS")) { //to handle CORS preflight
            HttpSession session = request.getSession(false);
            LabowletState applicationState = LabowletState.getInstance();
            GameSession userSession = applicationState.getGameSession(session);
            if(userSession == null){
                log.info("No valid session was found with the given x-auth-token {}", request.getHeader("x-auth-token"));
                JsonErrorResponseHandler.sendErrorResponse(response, HttpStatus.BAD_REQUEST,
                        new IllegalStateException("No valid session has been found! " +
                                "Please make sure you have the x-auth-token header and that it has a valid token."));
                return false;
            }
        }

        return true;
    }
}