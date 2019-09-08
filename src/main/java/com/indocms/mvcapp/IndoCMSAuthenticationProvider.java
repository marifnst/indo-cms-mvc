package com.indocms.mvcapp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class IndoCMSAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        System.out.println("IndoCMSAuthenticationProvider : " + username + " : " + password);

        System.out.println("authentication.getAuthorities() : " + authentication.getAuthorities());
        
        if (username.equals("user") && password.equals("user")) {
            // List<GrantedAuthority> lga = new ArrayList<>();
            // lga.add(new SimpleGrantedAuthority("/template/**"));
            return new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>());
        } else if (username.equals("admin") && password.equals("admin")) {
            List<GrantedAuthority> lga = new ArrayList<>();
            lga.add(new SimpleGrantedAuthority("/template/**"));
            // return new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>());
            return new UsernamePasswordAuthenticationToken(username, password, lga);
        } else {
            return null;
        }        
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}