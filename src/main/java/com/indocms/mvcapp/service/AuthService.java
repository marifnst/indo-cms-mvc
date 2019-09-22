package com.indocms.mvcapp.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Value("${app.database.service}")
    private String databaseService;
    
    public Map<String, Object> getUser(String username) throws Exception {
        Map<String, Object> output = new HashMap<>();
        String query = String.format("SELECT * FROM \"INDO_CMS\".PUBLIC.INDO_CMS_USER WHERE USERNAME = '%s'", username);
        List<Map<String, Object>> queryUserResult = DatabaseFactoryService.getService(databaseService).executeQuery(query);
        if (queryUserResult.size() > 0) {
            output = queryUserResult.get(0);
        }
        return output;
    }
    
    public String getCurrentUser() {
        String output = null;
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        output = auth.getName();
        return output;
    }

    public String getCurrentUserRole() {
        String output = null;
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> grantedAuthorityList = (List<GrantedAuthority>) auth.getAuthorities();
        output = grantedAuthorityList.get(0).getAuthority();
        return output;
    }
}