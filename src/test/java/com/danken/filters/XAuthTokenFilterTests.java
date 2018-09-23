package com.danken.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.util.*;

import static org.mockito.Mockito.*;

public class XAuthTokenFilterTests {

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;


    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PrintWriter printer = mock(PrintWriter.class);
        doReturn(printer).when(response).getWriter();
    }


    @Test
    public void xAuthTokenHeaderPresent() throws Exception {

        XAuthTokenFilter filter = new XAuthTokenFilter();
        FilterChain filterChain = spy(FilterChain.class);
        HttpServletWrapper requestWrapper = new HttpServletWrapper(request);
        requestWrapper.addHeader("x-auth-token");
        filter.doFilter(requestWrapper, response, filterChain);
        verify(filterChain).doFilter(requestWrapper, response);
    }

    @Test
    public void xAuthTokenHeaderNotPresent() throws Exception {
        XAuthTokenFilter filter = new XAuthTokenFilter();
        FilterChain filterChain = spy(FilterChain.class);
        filter.doFilter(request, response, filterChain);
        verify(filterChain, times(0)).doFilter(request, response);

    }


}

class HttpServletWrapper extends HttpServletRequestWrapper {

    List<String> headerNames;

    public HttpServletWrapper(HttpServletRequest request) {
        super(request);
        headerNames = new ArrayList<>();
    }

    public void addHeader(String headerName){
        headerNames.add(headerName);
    }

    public String getHeader(String name) {
        String header = super.getHeader(name);
        return (header != null) ? header : super.getParameter(name); // Note: you can't use getParameterValues() here.
    }

    public Enumeration getHeaderNames() {
        return Collections.enumeration(headerNames);
    }
}



