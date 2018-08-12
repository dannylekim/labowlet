package filters;

import servlet.LabowletState;
import sessions.GameSession;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;


public class RoomAuthFilter implements Filter {
    private LabowletState applicationState;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        applicationState = LabowletState.getInstance();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpSession session = ((HttpServletRequest) request).getSession();
        GameSession requestGameSession = applicationState.getGameSession(session);
        String roomCode = requestGameSession.getRoomId();
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
