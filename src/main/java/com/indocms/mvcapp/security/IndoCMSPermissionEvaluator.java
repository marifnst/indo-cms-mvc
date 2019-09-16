package com.indocms.mvcapp.security;

import java.io.Serializable;
import java.util.List;

import com.indocms.mvcapp.service.MenuService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class IndoCMSPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private MenuService menuService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        boolean output = false;
        // System.out.println(targetDomainObject + " : " + permission);        
        // System.out.println("credential : " + authentication.getCredentials());
        // System.out.println("details : " + authentication.getDetails());
        // System.out.println("name : " + authentication.getName());
        // System.out.println("principal : " + authentication.getPrincipal());
        // System.out.println("authorities : " + authentication.getAuthorities());

        List<GrantedAuthority> authority = (List<GrantedAuthority>) authentication.getAuthorities();
        try {            
            // if (menuService == null) {
            //     System.out.println("menuService is null");
            // }
            output = menuService.isAuthorized(authority.get(0).getAuthority(), targetDomainObject.toString(), permission.toString());
        } catch (Exception e) {
            e.printStackTrace();            
        }        
        
        return output;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        System.out.println(authentication + " : " + targetId + " : " + targetType + " : " + permission);
        return false;
    }

}