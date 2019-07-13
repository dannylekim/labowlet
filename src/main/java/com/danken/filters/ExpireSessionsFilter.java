package com.danken.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.danken.LabowletState;

import lombok.extern.slf4j.Slf4j;

/***
 *
 * This filter checks all com.danken.sessions and sees which ones are meant to be expired or not. Generally done by looking at
 * last accessed time and duration and checking its current system date.
 *
 * This filter should be configured with * for paths.
 *
 */
@Slf4j
public class ExpireSessionsFilter implements Filter {

    private LabowletState state = LabowletState.getInstance();

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        state.removeExpiredSessions();
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
