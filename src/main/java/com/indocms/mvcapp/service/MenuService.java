package com.indocms.mvcapp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class MenuService {

    @Value("${app.database.service}")
    private String databaseService;

    @Value("${app.breadcrumb.delimiter}")
    private String breadcrumbDelimiter;

    @Autowired
    private SessionService sessionService;

    public Map<String, Object> getSessionMenu(String url) throws Exception {
        List<Map<String, Object>> parentMenuList = (List<Map<String, Object>>) sessionService.getSession(url);
        Map<String, Object> menuPermission = parentMenuList.get(0);

        String breadcrumb = menuPermission.get("menu_breadcrumb").toString();
        menuPermission.put("menu_breadcrumb_list", Arrays.asList(breadcrumb.split("\\" + breadcrumbDelimiter)));

        return menuPermission;
    }
    
    public List<Map<String, Object>> getMenu() throws Exception {
        List<Map<String, Object>> parentMenuList = new ArrayList<>();
        String query = String.format("SELECT * FROM \"INDO_CMS\".PUBLIC.INDO_CMS_MENU WHERE MENU_PARENT_ID IS NULL OR MENU_PARENT_ID = '''' ORDER BY MENU_SEQUENCE");
        parentMenuList = DatabaseFactoryService.getService(databaseService).executeQuery(query);
        
        for (Map<String, Object> parentMenu : parentMenuList) {
            String tmpParentId = parentMenu.get("menu_id").toString();
            query = String.format("SELECT * FROM \"INDO_CMS\".PUBLIC.INDO_CMS_MENU WHERE MENU_PARENT_ID = '%s' ORDER BY MENU_SEQUENCE", tmpParentId);
            List<Map<String, Object>> childMenuList = DatabaseFactoryService.getService(databaseService).executeQuery(query);
            parentMenu.put("child", childMenuList);
        }

        return parentMenuList;
    }

    public List<Map<String, Object>> getMenuByAuthority() throws Exception {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> authorityList = (List<GrantedAuthority>) auth.getAuthorities();
        String authority = authorityList.get(0).getAuthority();

        List<Map<String, Object>> parentMenuList = new ArrayList<>();
        String query = String.format("SELECT A.MENU_ID, A.MENU_URL_TITLE, A.MENU_URL "+
        "FROM INDO_CMS_MENU A " + 
        "INNER JOIN INDO_CMS_MENU_PERMISSION B ON A.MENU_ID = B.MENU_ID "
        + " WHERE (A.MENU_PARENT_ID IS NULL OR A.MENU_PARENT_ID = '''') AND B.CAN_VIEW = '1' AND B.ROLE_ID = '%s' ORDER BY A.MENU_SEQUENCE", authority);
        parentMenuList = DatabaseFactoryService.getService(databaseService).executeQuery(query);
        
        for (Map<String, Object> parentMenu : parentMenuList) {
            String tmpParentId = parentMenu.get("menu_id").toString();
            query = String.format("SELECT A.MENU_URL_TITLE, A.MENU_URL " +
            "FROM INDO_CMS_MENU A " + 
            "INNER JOIN INDO_CMS_MENU_PERMISSION B ON A.MENU_ID = B.MENU_ID "
            + " WHERE A.MENU_PARENT_ID = '%s' AND B.CAN_VIEW = '1' AND B.ROLE_ID = '%s' ORDER BY A.MENU_SEQUENCE", tmpParentId, authority);
            List<Map<String, Object>> childMenuList = DatabaseFactoryService.getService(databaseService).executeQuery(query);
            parentMenu.put("child", childMenuList);
        }

        return parentMenuList;
    }

    public boolean isAuthorized(String role, String url, String action) throws Exception {
        boolean output = false;

        List<Map<String, Object>> parentMenuList = new ArrayList<>();
        String query = String.format("SELECT A.ROLE_ID, B.MENU_TITLE, B.MENU_SUBTITLE, B.MENU_BREADCRUMB, B.MENU_URL, "+
        "C.CAN_VIEW, C.CAN_INSERT, C.CAN_UPDATE, C.CAN_DELETE, C.CAN_EXPORT, C.CAN_IMPORT, C.IS_NEED_APPROVAL " + 
        "FROM INDO_CMS_ROLE A " + 
        "INNER JOIN INDO_CMS_MENU B ON B.MENU_URL = '%s' " + 
        "INNER JOIN INDO_CMS_MENU_PERMISSION C ON B.MENU_ID = C.MENU_ID AND A.ROLE_ID = C.ROLE_ID " + 
        "WHERE A.ROLE_ID = '%s'", url, role);
        // System.out.println("isAuthorized : " + query);

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        if (attr.getRequest().getSession().getAttribute(url) == null) {
            System.out.println("Session Null : " + url);
            parentMenuList = DatabaseFactoryService.getService(databaseService).executeQuery(query);
        } else {
            System.out.println("Session Not Null : " + url);
            parentMenuList = (List<Map<String, Object>>) attr.getRequest().getSession().getAttribute(url);
        }

        if (parentMenuList.size() > 0) {
            attr.getRequest().getSession().setAttribute(url, parentMenuList);
            switch(action.toLowerCase()) {
                case "view":{
                    output = parentMenuList.get(0).get("can_view").toString() != null && parentMenuList.get(0).get("can_view").toString().equals("1") ? true : false;
                    break;
                }
                case "insert":{
                    output = parentMenuList.get(0).get("can_insert").toString() != null && parentMenuList.get(0).get("can_insert").toString().equals("1") ? true : false;
                    break;
                }
                case "update":{
                    output = parentMenuList.get(0).get("can_update").toString() != null && parentMenuList.get(0).get("can_update").toString().equals("1") ? true : false;
                    break;
                }
                case "delete":{
                    output = parentMenuList.get(0).get("can_delete").toString() != null && parentMenuList.get(0).get("can_delete").toString().equals("1") ? true : false;
                    break;
                }
                case "export":{
                    output = parentMenuList.get(0).get("can_export").toString() != null && parentMenuList.get(0).get("can_export").toString().equals("1") ? true : false;
                    break;
                }
                case "import":{
                    output = parentMenuList.get(0).get("can_import").toString() != null && parentMenuList.get(0).get("can_import").toString().equals("1") ? true : false;
                    break;
                }
            }
        }
        return output;
    }

    public boolean isNeedApproval(String url) {
        boolean output = false;
        List<Map<String, Object>> screenInfo = (List<Map<String, Object>>) sessionService.getSession(url);
        if (screenInfo.get(0).get("is_need_approval") != null && screenInfo.get(0).get("is_need_approval").toString().equals("1")) {
            output = true;
        }
        return output;
    }
}