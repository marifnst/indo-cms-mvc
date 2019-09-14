package com.indocms.mvcapp;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.GenericFilterBean;

public class IndoCMSPreFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        /*HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String url = httpServletRequest.getRequestURL().toString();
        String uri = httpServletRequest.getRequestURI().toString();

        if (!uri.contains("/logout") && !uri.contains("/login") && !uri.contains("/error") && !uri.contains("/home")) {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                String username = null;
                if (principal instanceof UserDetails) {
                    username = ((UserDetails) principal).getUsername();
                } else {
                    username = principal.toString();
                }

                System.out.println("masuk filter : " + url + " : " + httpServletRequest.getRequestURI());        
                System.out.println("principal : " + principal);
                if (username.equals("admin") && uri.contains("/template")) {

                } else {
                    httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid authentication.");
                    return ;
                }
            }
        }*/
        chain.doFilter(request, response);
    }

}