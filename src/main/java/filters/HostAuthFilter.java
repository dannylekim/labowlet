package filters;

import javax.servlet.*;
import java.io.IOException;

/***
 *
 * This filter is meant to authorize hosts for certain room calls. It will check if the player in the session is
 * the host of the room for whatever paths it is configured to.
 *
 */
public class HostAuthFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //todo
    }

    @Override
    public void destroy() {

    }
}
