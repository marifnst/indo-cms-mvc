package com.indocms.mvcapp.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class SessionService {

    public boolean checkSession(String attributeName) {
        boolean output = false;
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        if (attr.getRequest().getSession().getAttribute(attributeName) != null) {
            output = true;
        }

        return output;
    }

    public void addSession(String attributeName, Object attributeValue) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        attr.getRequest().getSession().setAttribute(attributeName, attributeValue);        
    }

    public Object getSession(String attributeName) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        Object output = attr.getRequest().getSession().getAttribute(attributeName);
        return output;
    }
}