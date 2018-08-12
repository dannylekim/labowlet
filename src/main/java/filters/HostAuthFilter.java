package filters;

import javax.servlet.*;
import java.io.IOException;

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
