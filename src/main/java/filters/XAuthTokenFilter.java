package filters;

import org.springframework.http.HttpStatus;
import utility.JsonErrorResponseHandler;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/***
 *
 * This filter checks all sessions and sees which ones are meant to be expired or not. Generally done by looking at
 * last accessed time and duration and checking its current system date.
 *
 * This filter should be configured with * for paths.
 *
 */
public class XAuthTokenFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Enumeration<String> headerNames = ((HttpServletRequest) request).getHeaderNames();

        boolean xAuthTokenHeaderExists = false;
        while (headerNames != null && headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (headerName.equalsIgnoreCase("x-auth-token")) {
                xAuthTokenHeaderExists = true;
                break;
            }
        }

        if (xAuthTokenHeaderExists) {
            chain.doFilter(request, response);
        } else {
            JsonErrorResponseHandler.sendErrorResponse((HttpServletResponse) response,
                    HttpStatus.FORBIDDEN, new IllegalAccessError("X-Auth-Token header is not present! Can not process request" +
                            "without this header."));
        }


    }

    @Override
    public void destroy() {

    }
}
