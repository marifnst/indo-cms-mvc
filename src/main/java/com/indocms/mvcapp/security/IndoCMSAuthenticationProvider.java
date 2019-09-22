package com.indocms.mvcapp.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.indocms.mvcapp.service.AuthService;
import com.indocms.mvcapp.service.SessionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class IndoCMSAuthenticationProvider implements AuthenticationProvider {

    @Value("${app.database.service}")
    private String databaseService;

    @Autowired
    private AuthService authService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            Map<String, Object> userInfo = authService.getUser(username);
            if (userInfo.keySet().size() > 0) {
                String dbPassword = userInfo.get("password").toString();

                BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
                if (bCryptPasswordEncoder.matches(password, dbPassword)) {
                    String roleId = userInfo.get("role_id").toString();
                    List<GrantedAuthority> lga = new ArrayList<>();
                    lga.add(new SimpleGrantedAuthority(roleId));
                    return new UsernamePasswordAuthenticationToken(username, password, lga);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }        

        /*
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
        }*/
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}