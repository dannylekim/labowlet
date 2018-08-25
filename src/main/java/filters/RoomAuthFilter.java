package filters;

import application.LabowletState;
import sessions.GameSession;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/***
 *
 * This filter deals with all authorization that the room may need such as are you doing a change to a room that you are
 * currently in or not.
 *
 *
 */
public class RoomAuthFilter implements Filter {
    private LabowletState applicationState;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        applicationState = LabowletState.getInstance();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpSession session = ((HttpServletRequest) request).getSession(false);
        GameSession requestGameSession = applicationState.getGameSession(session);
        String roomCode = requestGameSession.getRoomCode();
        String requestRoomCode = request.getParameter("roomCode");
        if(roomCode.equals(requestRoomCode)){
            chain.doFilter(request,response);
        }
        else {
            //todo return 403
        }
    }

    @Override
    public void destroy() {}
}
