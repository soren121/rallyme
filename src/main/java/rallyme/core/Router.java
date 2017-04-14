package rallyme.core;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(filterName="Router", urlPatterns={"/*"})
public class Router implements Filter {

    @Override
    public void init(FilterConfig fc) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc) throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;
        String uri = httpRequest.getRequestURI();
        String servletPath = httpRequest.getServletPath();

        // Define regex for pages that require authentication
        Pattern authRegex = Pattern.compile("^/(?:Dashboard|AddRally|EditRally|Profile)");
        Matcher authMatcher = authRegex.matcher(servletPath);

        // If this is an authenticated page but user isn't logged in, redirect to Login page
        HttpSession session = httpRequest.getSession(false);
        boolean noAuth = (session == null || session.isNew() || session.getAttribute("user") == null);
        if(authMatcher.find() && noAuth) {
            httpResponse.sendRedirect("Login");
        }

        fc.doFilter(request, response);
    }

    @Override
    public void destroy() {}
    
}
