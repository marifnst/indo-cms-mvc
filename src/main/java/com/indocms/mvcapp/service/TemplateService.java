package com.indocms.mvcapp.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TemplateService {

    @Value("${app.database.service}")
    private String databaseService;

    // private Map<String, Object> templateHeader = null;
    // private List<Map<String, Object>> templateDetail = null;

    public Map<String, Object> viewService(String templateCode) throws Exception {
        Map<String, Object> output = new HashMap<>();

        Map<String, Object> templateHeader = this.getTemplateHeader(templateCode);

        String templateDetailFilter = " AND is_show = '1'";
        List<Map<String, Object>> templateDetail = this.getTemplateDetail(templateCode, templateDetailFilter);
        output.put("template_detail", templateDetail);

        List<Map<String, Object>> templateData = this.getTemplateData(templateHeader, templateDetail);
        output.put("template_data", templateData);

        return output;
    }

    public Map<String, Object> createService(String templateCode) throws Exception {
        Map<String, Object> output = new HashMap<>();

        String templateDetailFilter = " AND is_create = '1'";
        List<Map<String, Object>> templateDetail = this.getTemplateDetail(templateCode, templateDetailFilter);
        output.put("template_detail", templateDetail);

        return output;
    }

    public Map<String, Object> getTemplateHeader(String templateCode) throws Exception {
        Map<String, Object> output = new HashMap<>();
        String query = String.format("SELECT * FROM \"INDO_CMS\".PUBLIC.INDO_CMS_TEMPLATE_HEADER WHERE TEMPLATE_CODE = '%s'", templateCode);
        List<Map<String, Object>> templateHeaderList = DatabaseFactoryService.getService(databaseService).executeQuery(query);
        if (templateHeaderList.size() > 0) {
            // System.out.println(templateHeaderList.get(0));
            output = templateHeaderList.get(0);
        }
        return output;
    }

    public List<Map<String, Object>> getTemplateDetail(String templateCode, String filter) throws Exception {
        List<Map<String, Object>> output = new ArrayList<>();
        String query = String.format("SELECT * FROM \"INDO_CMS\".PUBLIC.INDO_CMS_TEMPLATE_DETAIL WHERE TEMPLATE_CODE = '%s' %s ORDER BY SEQUENCE", templateCode, filter);
        output = DatabaseFactoryService.getService(databaseService).executeQuery(query);
        // System.out.println(output);
        return output;
    }

    public List<Map<String, Object>> getTemplateData(Map<String, Object> templateHeader, List<Map<String, Object>> templateDetail)  throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        for (int i= 0;i < templateDetail.size(); i++) {
            String queryColumn = templateDetail.get(i).get("query_column").toString().trim();
            String databaseColumn = templateDetail.get(i).get("query_column").toString().trim();
            query.append(queryColumn).append(" AS ").append(databaseColumn);

            if (i + 1 < templateDetail.size()) {
                query.append(", ");
            }
        }

        String databaseName = templateHeader.get("database_name").toString().trim();
        String databaseTableDelimiter = templateHeader.get("database_table_delimiter").toString().trim();
        String tableName = templateHeader.get("table_name").toString().trim();
        query.append(" FROM ").append(databaseName).append(databaseTableDelimiter).append(tableName);
        // System.out.println(query);
        templateHeader.put("query", query);

        List<Map<String, Object>> output = DatabaseFactoryService.getService(databaseService).executeQuery(templateHeader);
        // System.out.println(output);
        return output;
    }
}