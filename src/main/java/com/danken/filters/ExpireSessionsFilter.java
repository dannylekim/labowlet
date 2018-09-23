package com.danken.filters;

import com.danken.application.LabowletState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;

/***
 *
 * This filter checks all com.danken.sessions and sees which ones are meant to be expired or not. Generally done by looking at
 * last accessed time and duration and checking its current system date.
 *
 * This filter should be configured with * for paths.
 *
 */
public class ExpireSessionsFilter implements Filter{

    LabowletState state = LabowletState.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(ExpireSessionsFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        logger.debug("Removing expired com.danken.sessions...");
        state.removeExpiredSessions();
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
