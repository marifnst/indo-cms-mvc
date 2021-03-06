package com.indocms.mvcapp.service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TemplateService {

    @Value("${app.single.database}")
    private boolean isSingleDatabase;

    @Value("${app.database.service}")
    private String databaseService;

    @Value("${app.export.pattern.date}")
    private String exportPatternDate;

    @Value("${app.breadcrumb.delimiter}")
    private String breadcrumbDelimiter;

    @Autowired
    private ExportService exportService;

    @Autowired
    private ImportService importService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private ApprovalService approvalService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private GeneralService generalService;

    // private Map<String, Object> templateHeader = null;
    // private List<Map<String, Object>> templateDetail = null;

    public Map<String, Object> viewService(String templateCode) throws Exception {
        Map<String, Object> output = new HashMap<>();

        Map<String, Object> templateHeader = this.getTemplateHeader(templateCode);

        String templateDetailFilter = " AND is_show = '1'";
        List<Map<String, Object>> templateDetail = this.getTemplateDetail(templateCode, templateDetailFilter);
        output.put("template_detail", templateDetail);

        String url = "template/view/" + templateCode;
        // ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        // List<Map<String, Object>> menuPermissionList = (List<Map<String, Object>>) attr.getRequest().getSession().getAttribute(url);
        // Map<String, Object> menuPermission = menuPermissionList.get(0);
        List<Map<String, Object>> menuPermissionList = (List<Map<String, Object>>) sessionService.getSession(url);
        List<Map<String, Object>> templateData = this.getTemplateData(templateHeader, templateDetail, menuPermissionList);

        Map<String, Object> menuPermission = menuService.getSessionMenu(url);
        // String breadcrumb = menuPermission.get("menu_breadcrumb").toString();
        // menuPermission.put("menu_breadcrumb_list", Arrays.asList(breadcrumb.split("\\" + breadcrumbDelimiter)));

        output.put("template_data", templateData);
        output.put("menu", menuPermission);
        // output.put("can_insert", canInsert);
        // output.put("can_export", canExport);
        // output.put("can_import", canImport);

        return output;
    }

    //#region create service
    public Map<String, Object> createService(String templateCode, String url) throws Exception {
        Map<String, Object> output = new HashMap<>();

        Map<String, Object> templateHeader = this.getTemplateHeader(templateCode);

        String templateDetailFilter = " AND is_create = '1'";
        List<Map<String, Object>> templateDetail = this.getTemplateDetail(templateCode, templateDetailFilter);

        for (Map<String, Object> rowDetail : templateDetail) {
            String dataType = rowDetail.get("data_type").toString().toLowerCase();
            if (dataType.equals("lookup")) {
                List<Map<String, Object>> tmpLookupOutput = this.getLookupData(templateHeader, rowDetail);
                rowDetail.put("lookup_data", tmpLookupOutput);
            }
        }

        // List<Map<String, Object>> menuPermissionList = (List<Map<String, Object>>) sessionService.getSession(url);
        // Map<String, Object> menuPermission = menuPermissionList.get(0);

        // String breadcrumb = menuPermission.get("menu_breadcrumb").toString();
        // menuPermission.put("menu_breadcrumb_list", Arrays.asList(breadcrumb.split("\\" + breadcrumbDelimiter)));

        Map<String, Object> menuPermission = menuService.getSessionMenu(url);

        output.put("template_detail", templateDetail);
        output.put("menu", menuPermission);

        return output;
    }

    public Map<String, String> createProcessService(String url, String templateCode, String payload) throws Exception {
        Map<String, String> output = new HashMap<>();

        HashMap<String,Object> payloadMap = new ObjectMapper().readValue(payload, HashMap.class);
        System.out.println("createProcessService : " + payloadMap);

        Map<String, Object> templateHeader = this.getTemplateHeader(templateCode);

        String databaseName = templateHeader.get("database_name").toString().trim();
        String databaseTableDelimiter = templateHeader.get("database_table_delimiter").toString().trim();
        String tableName = templateHeader.get("table_name").toString().trim();

        StringBuilder query = new StringBuilder();
        StringBuilder queryValues = new StringBuilder("(");

        query.append("INSERT INTO ").append(databaseName).append(databaseTableDelimiter).append(tableName).append(" (");
        Set<String> keySet = payloadMap.keySet();
        int count = 0;
        for (String key : keySet) {
            query.append(key);

            String keyValue = payloadMap.get(key).toString();
            queryValues.append("'").append(keyValue).append("'");

            if (count + 1 < keySet.size()) {
                query.append(", ");
                queryValues.append(", ");
            }
            count++;
        }
        query.append(")");
        queryValues.append(")");

        query.append(" VALUES ").append(queryValues.toString());
        // System.out.println("query insert : " + query);
        templateHeader.put("query", query);

        List<Map<String, Object>> queryOutput = null;
        if (menuService.isNeedApproval(url)) {
            approvalService.crudApprovalnjection(templateCode, "INSERT", "NULL", "'" + payload + "'", "NULL", query.toString());
            output.put("status", "Success");
            output.put("message", "Insert Request Has Been Submitted, Please Kindly Wait For Approval");
        } else {
            if (isSingleDatabase) {
                queryOutput = DatabaseFactoryService.getService(databaseService).executeUpdate(query.toString());
            } else {
                queryOutput = DatabaseFactoryService.getService(databaseService).executeUpdate(templateHeader);
            }
            output.put("status", "Success");
            output.put("message", "Insert Data Success");
        }
        System.out.println("queryOutput : " + queryOutput);

        return output;
    }

    //#endregion create servcie

    //#region edit service
    public Map<String, Object> editService(String templateCode, String dataId, String url) throws Exception {
        Map<String, Object> output = new HashMap<>();

        Map<String, Object> templateHeader = this.getTemplateHeader(templateCode);

        String templateDetailFilter = " AND CASE WHEN is_edit = '1' THEN '1' WHEN is_primary = '1' THEN '1' ELSE '0' END = '1'";
        List<Map<String, Object>> templateDetail = this.getTemplateDetail(templateCode, templateDetailFilter);

        this.getEditData(templateHeader, templateDetail, dataId);

        for (Map<String, Object> rowDetail : templateDetail) {
            String dataType = rowDetail.get("data_type").toString().toLowerCase();
            if (dataType.equals("lookup")) {
                List<Map<String, Object>> tmpLookupOutput = this.getLookupData(templateHeader, rowDetail);
                rowDetail.put("lookup_data", tmpLookupOutput);
            }
        }

        Map<String, Object> menuPermission = menuService.getSessionMenu(url);
        output.put("template_detail", templateDetail);
        output.put("menu", menuPermission);

        return output;
    }

    public Map<String, String> editProcessService(String url, String templateCode, String dataId, String payload) throws Exception {
        Map<String, String> output = new HashMap<>();

        HashMap<String,Object> payloadMap = new ObjectMapper().readValue(payload, HashMap.class);
        System.out.println("editProcessService : " + payloadMap);

        Map<String, Object> templateHeader = this.getTemplateHeader(templateCode);

        String databaseName = templateHeader.get("database_name").toString().trim();
        String databaseTableDelimiter = templateHeader.get("database_table_delimiter").toString().trim();
        String tableName = templateHeader.get("table_name").toString().trim();

        StringBuilder query = new StringBuilder();
        query.append("UPDATE ").append(databaseName).append(databaseTableDelimiter).append(tableName);
        query.append(" SET ");

        Set<String> keySet = payloadMap.keySet();
        int count = 0;
        for (String key : keySet) {
            String keyValue = payloadMap.get(key).toString();
            query.append(key).append(" = '").append(keyValue).append("'");

            if (count + 1 < keySet.size()) {
                query.append(", ");
            }
            count++;
        }
        query.append(" WHERE row_id = '").append(dataId).append("'");
        templateHeader.put("query", query);
        // System.out.println("query edit : " + query);

        List<Map<String, Object>> queryOutput = null;
        if (menuService.isNeedApproval(url)) {
            String templateDetailFilter = " AND CASE WHEN is_edit = '1' THEN '1' WHEN is_primary = '1' THEN '1' ELSE '0' END = '1'";
            List<Map<String, Object>> templateDetail = this.getTemplateDetail(templateCode, templateDetailFilter);

            Map<String, Object> dataBefore = this.getDataById(templateHeader, templateDetail, dataId);
            String dataBeforeString = new ObjectMapper().writeValueAsString(dataBefore);
            approvalService.crudApprovalnjection(templateCode, "UPDATE", "'" + dataBeforeString + "'", "'" + payload + "'", "NULL", query.toString());
            output.put("status", "Success");
            output.put("message", "Edit Request Has Been Submitted, Please Kindly Wait For Approval");
        } else {
            if (isSingleDatabase) {
                queryOutput = DatabaseFactoryService.getService(databaseService).executeUpdate(query.toString());
            } else {
                queryOutput = DatabaseFactoryService.getService(databaseService).executeUpdate(templateHeader);
            }
            // queryOutput = DatabaseFactoryService.getService(databaseService).executeUpdate(templateHeader);
            output.put("status", "Success");
            output.put("message", "Edit Data Success");
        }
        // System.out.println("queryOutput edit : " + queryOutput);
        return output;
    }
    //#endregion edit service

    //#region delete service
    public Map<String, String> deleteProcessService(String url, String templateCode, String dataId) throws Exception {
        Map<String, String> output = new HashMap<>();
        Map<String, Object> templateHeader = this.getTemplateHeader(templateCode);

        String databaseName = templateHeader.get("database_name").toString().trim();
        String databaseTableDelimiter = templateHeader.get("database_table_delimiter").toString().trim();
        String tableName = templateHeader.get("table_name").toString().trim();

        StringBuilder query = new StringBuilder();
        query.append("DELETE FROM ").append(databaseName).append(databaseTableDelimiter).append(tableName);
        query.append(" WHERE row_id = '").append(dataId).append("'");
        // System.out.println("query delete : " + query);
        templateHeader.put("query", query);

        List<Map<String, Object>> queryOutput = null;
        if (menuService.isNeedApproval(url)) {
            String templateDetailFilter = " AND is_show = '1'";
            List<Map<String, Object>> templateDetail = this.getTemplateDetail(templateCode, templateDetailFilter);

            Map<String, Object> dataBefore = this.getDataById(templateHeader, templateDetail, dataId);
            String dataBeforeString = new ObjectMapper().writeValueAsString(dataBefore);

            approvalService.crudApprovalnjection(templateCode, "DELETE", "'" + dataBeforeString + "'", "NULL", "NULL", query.toString());
            output.put("status", "Success");
            output.put("message", "Delete Request Has Been Submitted, Please Kindly Wait For Approval");
        } else {
            if (isSingleDatabase) {
                queryOutput = DatabaseFactoryService.getService(databaseService).executeUpdate(query.toString());
            } else {
                queryOutput = DatabaseFactoryService.getService(databaseService).executeUpdate(templateHeader);
            }
            // queryOutput = DatabaseFactoryService.getService(databaseService).executeUpdate(templateHeader);
            output.put("status", "Success");
            output.put("message", "Delete Data Success");
        }
        System.out.println("queryOutput delete : " + queryOutput);

        return output;
    }
    //#endregion delete service

    //#region export service
    public String exportProcessService(String templateCode, String payload) throws Exception {
        String output = null;
        HashMap<String,Object> payloadMap = new ObjectMapper().readValue(payload, HashMap.class);
        String exportType = payloadMap.get("export_type").toString();
        switch(exportType.toLowerCase()) {
            case "csv":{
                output = exportService.exportToCSV(templateCode);
                break;
            }
            case "xls":case "xlsx": {
                output = exportService.exportToExcel(exportType, templateCode);
                break;
            }
        }
        return output;
    }
    //#endregion

    //#region import process
    public Map<String, String> importProcessService(String url, String templateCode, MultipartFile file) throws Exception {
        Map<String, String> output = new HashMap<>();

        InputStream initialStream = file.getInputStream();

        String importFileExtension = generalService.getFileExtension(file.getOriginalFilename()).toLowerCase();
        String exportPathString = "IMPORT_" + templateCode + "_" + generalService.convertDateToString(new Date(), exportPatternDate) + "." + importFileExtension;

        Path exportPath = Paths.get(exportPathString);
        Files.copy(initialStream, exportPath, StandardCopyOption.REPLACE_EXISTING);
        IOUtils.closeQuietly(initialStream);

        if (menuService.isNeedApproval(url)) {
            approvalService.crudApprovalnjection(templateCode, "IMPORT", "NULL", "NULL", "'" + exportPathString + "'", "NULL");
            output.put("status", "Success");
            output.put("message", "Import Request Has Been Submitted, Please Kindly Wait For Approval");
        } else {
            switch(importFileExtension.toLowerCase()) {
                case "csv":{
                    importService.importCSV(templateCode, exportPathString);
                    break;
                }
                case "xls":case "xlsx": {
                    importService.importExcel(templateCode, exportPathString);
                    break;
                }
            }

            output.put("status", "Success");
            output.put("message", "Import Data Success");
        }
        return output;
    }
    //#endregion import process

    public Map<String, Object> getTemplateHeader(String templateCode) throws Exception {
        Map<String, Object> output = new HashMap<>();
        String query = String.format("SELECT * FROM INDO_CMS_TEMPLATE_HEADER WHERE TEMPLATE_CODE = '%s'", templateCode);
        List<Map<String, Object>> templateHeaderList = DatabaseFactoryService.getService(databaseService).executeQuery(query);
        if (templateHeaderList.size() > 0) {
            // System.out.println(templateHeaderList.get(0));
            output = templateHeaderList.get(0);
        }
        return output;
    }

    public List<Map<String, Object>> getTemplateDetail(String templateCode, String filter) throws Exception {
        List<Map<String, Object>> output = new ArrayList<>();
        String query = String.format("SELECT * FROM INDO_CMS_TEMPLATE_DETAIL WHERE TEMPLATE_CODE = '%s' %s ORDER BY SEQUENCE", templateCode, filter);
        output = DatabaseFactoryService.getService(databaseService).executeQuery(query);
        // System.out.println(output);
        return output;
    }

    public List<Map<String, Object>> getLookupData(Map<String, Object> templateHeader, Map<String, Object> lookupTemplate) throws Exception {
        List<Map<String, Object>> output = new ArrayList<>();

        if (lookupTemplate.get("lookup_query") != null && !lookupTemplate.get("lookup_query").toString().equals("")) {
            String query = lookupTemplate.get("lookup_query").toString();
            templateHeader.put("query", query);
            if (isSingleDatabase) {
                output = DatabaseFactoryService.getService(databaseService).executeQuery(query);
            } else {
                output = DatabaseFactoryService.getService(databaseService).executeQuery(templateHeader);
            }
        }
        // System.out.println(output);
        return output;
    }

    public List<Map<String, Object>> getTemplateData(Map<String, Object> templateHeader, List<Map<String, Object>> templateDetail, List<Map<String, Object>> menuPermissionList)  throws Exception {
        StringBuilder query = new StringBuilder();
        String templateCode = templateHeader.get("template_code").toString();

        query.append("SELECT ");

        String editColumn = "CONCAT('<a href=\"#\" onclick=\"return navigateToUrl(''template/edit/" + templateCode + "/', row_id, ''')\">Edit</a>')";

        String deleteColumn = "CONCAT('<a href=\"#\" onclick=\"return deleteProcess(''template/delete/process/" + templateCode + "/', row_id, ''')\">Delete</a>')";

        String canUpdate = menuPermissionList.get(0).get("can_update") != null && !menuPermissionList.get(0).get("can_update").equals("") ? menuPermissionList.get(0).get("can_update").toString() : "0";
        String canDelete = menuPermissionList.get(0).get("can_delete") != null && !menuPermissionList.get(0).get("can_delete").equals("") ? menuPermissionList.get(0).get("can_delete").toString() : "0";

        if (canUpdate.equals("1") && canDelete.equals("1")) {
            query.append("CONCAT(").append(editColumn).append(",").append("'&nbsp;',").append(deleteColumn).append(") AS ACTION_ROW, ");
        } else if (canUpdate.equals("1") && canDelete.equals("0")) {
            query.append(editColumn).append(" AS ACTION_ROW, ");
        }  else if (canUpdate.equals("0") && canDelete.equals("1")) {
            query.append(deleteColumn).append(" AS ACTION_ROW, ");
        } else {
            query.append("'' AS ACTION_ROW, ");
        }
        // query.append(deleteColumn).append(" AS ACTION_ROW, ");

        for (int i= 0;i < templateDetail.size(); i++) {
            String queryColumn = templateDetail.get(i).get("query_column").toString().trim();
            String databaseColumn = templateDetail.get(i).get("database_column").toString().trim();
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

        List<Map<String, Object>> output = new ArrayList<>();
        if (isSingleDatabase) {
            output = DatabaseFactoryService.getService(databaseService).executeQuery(query.toString());
        } else {
            output = DatabaseFactoryService.getService(databaseService).executeQuery(templateHeader);
        }
        // System.out.println(output);
        return output;
    }

    public void getEditData(Map<String, Object> templateHeader, List<Map<String, Object>> templateDetail, String dataId)  throws Exception {
        String databaseName = templateHeader.get("database_name").toString().trim();
        String databaseTableDelimiter = templateHeader.get("database_table_delimiter").toString().trim();
        String tableName = templateHeader.get("table_name").toString().trim();

        StringBuilder query = new StringBuilder();

        String primaryKeyColumn = null;
        query.append("SELECT ");
        for (int i= 0;i < templateDetail.size(); i++) {
            String queryColumn = templateDetail.get(i).get("query_column").toString().trim();
            String databaseColumn = templateDetail.get(i).get("database_column").toString().trim();
            if (templateDetail.get(i).get("is_primary") != null && templateDetail.get(i).get("is_primary").toString().equals("1")) {
                primaryKeyColumn = templateDetail.get(i).get("database_column").toString();
            } else {
                query.append(queryColumn).append(" AS ").append(databaseColumn);

                if (i + 1 < templateDetail.size()) {
                    query.append(", ");
                }
            }
        }
        query.append(" FROM ").append(databaseName).append(databaseTableDelimiter).append(tableName);
        query.append(" WHERE ").append(primaryKeyColumn).append(" = '").append(dataId).append("'");

        // System.out.println("get edit data query : " + query);
        templateHeader.put("query", query);

        List<Map<String, Object>> selectOutput = new ArrayList<>();
        if (isSingleDatabase) {
            selectOutput = DatabaseFactoryService.getService(databaseService).executeQuery(query.toString());
        } else {
            selectOutput = DatabaseFactoryService.getService(databaseService).executeQuery(templateHeader);
        }
        // List<Map<String, Object>> selectOutput = DatabaseFactoryService.getService(databaseService).executeQuery(templateHeader);
        Map<String, Object> mainData = selectOutput.get(0);

        int primaryIndex = 0;
        for (int i = 0;i < templateDetail.size(); i++) {
            if (templateDetail.get(i).get("is_primary") == null || templateDetail.get(i).get("is_primary").toString().equals("0")) {
                String columnName = templateDetail.get(i).get("database_column").toString();
                if (mainData.containsKey(columnName) && mainData.get(columnName) != null) {
                    String dataValue = mainData.get(columnName).toString();
                    templateDetail.get(i).put("edit_data", dataValue);
                }
            } else {
                primaryIndex = 0;
            }
        }

        // remove primary in template detail
        templateDetail.remove(primaryIndex);
    }

    public Map<String, Object> getDataById(Map<String, Object> templateHeader, List<Map<String, Object>> templateDetail, String dataId)  throws Exception {
        String databaseName = templateHeader.get("database_name").toString().trim();
        String databaseTableDelimiter = templateHeader.get("database_table_delimiter").toString().trim();
        String tableName = templateHeader.get("table_name").toString().trim();

        StringBuilder query = new StringBuilder();

        String primaryKeyColumn = null;
        query.append("SELECT ");
        for (int i= 0;i < templateDetail.size(); i++) {
            String queryColumn = templateDetail.get(i).get("query_column").toString().trim();
            String databaseColumn = templateDetail.get(i).get("database_column").toString().trim();
            if (templateDetail.get(i).get("is_primary") != null && templateDetail.get(i).get("is_primary").toString().equals("1")) {
                primaryKeyColumn = templateDetail.get(i).get("database_column").toString();
            } else {
                query.append(queryColumn).append(" AS ").append(databaseColumn);

                if (i + 1 < templateDetail.size()) {
                    query.append(", ");
                }
            }
        }
        query.append(" FROM ").append(databaseName).append(databaseTableDelimiter).append(tableName);
        query.append(" WHERE ").append(primaryKeyColumn).append(" = '").append(dataId).append("'");

        // System.out.println("get edit data query : " + query);
        templateHeader.put("query", query);

        // List<Map<String, Object>> selectOutput = DatabaseFactoryService.getService(databaseService).executeQuery(templateHeader);
        List<Map<String, Object>> selectOutput = new ArrayList<>();
        if (isSingleDatabase) {
            selectOutput = DatabaseFactoryService.getService(databaseService).executeQuery(query.toString());
        } else {
            selectOutput = DatabaseFactoryService.getService(databaseService).executeQuery(templateHeader);
        }        
        Map<String, Object> mainData = selectOutput.get(0);

        return mainData;
    }
}