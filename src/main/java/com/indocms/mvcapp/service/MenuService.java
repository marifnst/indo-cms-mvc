package com.indocms.mvcapp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MenuService {

    @Value("${app.database.service}")
    private String databaseService;
    
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

    public List<Map<String, Object>> getUserMenu(String username) throws Exception {
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

    public boolean isAuthorized(String role, String url, String action) throws Exception {
        boolean output = false;
        List<Map<String, Object>> parentMenuList = new ArrayList<>();
        String query = String.format("SELECT A.ROLE_ID, B.MENU_URL, C.CAN_VIEW, C.CAN_INSERT, C.CAN_UPDATE, C.CAN_DELETE, C.CAN_EXPORT, C.CAN_IMPORT " + 
        "FROM INDO_CMS_ROLE A " + 
        "INNER JOIN INDO_CMS_MENU B ON B.MENU_URL = '%s' " + 
        "INNER JOIN INDO_CMS_MENU_PERMISSION C ON B.MENU_ID = C.MENU_ID AND A.ROLE_ID = C.ROLE_ID " + 
        "WHERE A.ROLE_ID = '%s'", url, role);
        // System.out.println("isAuthorized : " + query);
        parentMenuList = DatabaseFactoryService.getService(databaseService).executeQuery(query);
        if (parentMenuList.size() > 0) {
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

}