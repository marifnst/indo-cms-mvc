package com.indocms.mvcapp.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ApprovalService {

    @Value("${app.database.service}")
    private String databaseService;

    @Value("${app.approval.type}")
    private String approvalType;

    @Value("${app.approval.pattern.date}")
    private String approvalPatternDate;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private ImportService importService;

    @Autowired
    private AuthService authService;

    @Autowired
    private GeneralService generalService;

    public void crudApprovalnjection(String templateCode, String approvalType, String payloadBefore, String payload, String dataImport, String crudQuery) throws Exception {
        StringBuilder query = new StringBuilder("INSERT INTO INDO_CMS_APPROVAL_HEADER (");
        query
            .append("approval_status, approval_type, template_code, approval_created_by, approval_created_date, approval_data_before, ")
            .append("approval_data_after, approval_data_import, approval_query) VALUES (")
            .append("'%s', '%s', '%s', '%s', '%s', %s, %s, %s, '%s')");
                        
        String approvalPendingFlag = "PENDING";
        String createdBy = authService.getCurrentUser();
        // String createdDate = generalService.convertDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        String createdDate = generalService.convertDateToString(new Date(), approvalPatternDate);
        String dataBefore = payloadBefore;
        String dataAfter = payload;
        // String dataImport = "NULL";
        String queryFinal = query.toString();
        queryFinal = String.format(queryFinal, approvalPendingFlag, approvalType, templateCode, createdBy, createdDate, dataBefore, dataAfter, dataImport, crudQuery.replaceAll("'", "''"));
        System.out.println("crudApprovalnjection : " + queryFinal);
        
        DatabaseFactoryService.getService(databaseService).executeUpdate(queryFinal);
    }

    public List<Map<String, Object>> getApprovalTaskList() throws Exception {
        // String username = authService.getCurrentUser();
        // StringBuilder query = new StringBuilder("SELECT * FROM \"INDO_CMS\".PUBLIC.INDO_CMS_APPROVAL_HEADER WHERE APPROVAL_STATUS = 'PENDING' ORDER BY APPROVAL_CREATED_DATE DESC");
        String currentUser = authService.getCurrentUser();
        String currentUserRole = authService.getCurrentUserRole();
        StringBuilder query = new StringBuilder("SELECT A.* FROM \"INDO_CMS\".PUBLIC.INDO_CMS_APPROVAL_HEADER A");                
        
        switch (approvalType) {
            case "SINGLE_USER_APPROVAL" : {                
                query.append(" INNER JOIN \"INDO_CMS\".PUBLIC.INDO_CMS_USER_APPROVER B ON");
                query.append(" A.APPROVAL_CREATED_BY = B.USERNAME AND B.APPROVER_USERNAME = '").append(currentUser).append("'");
                break;
            }
            case "SINGLE_ROLE_APPROVAL" : {
                query.append(" INNER JOIN \"INDO_CMS\".PUBLIC.INDO_CMS_USER B ON A.APPROVAL_CREATED_BY = B.USERNAME");
                query.append(" INNER JOIN \"INDO_CMS\".PUBLIC.INDO_CMS_USER_APPROVER C ON B.ROLE_ID = C.ROLE_ID");
                query.append(" AND C.APPROVER_ROLE = '").append(currentUserRole).append("'");
                break;
            }
        }

        query.append(" WHERE A.APPROVAL_STATUS = 'PENDING'");
        query.append(" ORDER BY A.APPROVAL_CREATED_DATE DESC");

        List<Map<String, Object>> approvalTaskList = DatabaseFactoryService.getService(databaseService).executeQuery(query.toString());
        // System.out.println("approvalTaskList : " + approvalTaskList);
        // Map<String, Object> output = new HashMap<>();
        // output.put("approval_task_list", approvalTaskList);
        return approvalTaskList;
    }

    public List<Map<String, Object>> getApprovalHistory() throws Exception {
        String username = authService.getCurrentUser();
        String query = String.format("SELECT * FROM \"INDO_CMS\".PUBLIC.INDO_CMS_APPROVAL_HEADER WHERE APPROVAL_CREATED_BY = '%s' ORDER BY APPROVAL_CREATED_DATE DESC", username);
        List<Map<String, Object>> approvalTaskList = DatabaseFactoryService.getService(databaseService).executeQuery(query.toString());
        return approvalTaskList;
    }

    public List<Map<String, Object>> getApprovalDetailHistory(String approvalId) throws Exception {
        String query = String.format("SELECT * FROM \"INDO_CMS\".PUBLIC.INDO_CMS_APPROVAL_DETAIL WHERE APPROVAL_HEADER_ID = '%s' ORDER BY APPROVAL_DATE", approvalId);
        List<Map<String, Object>> output = DatabaseFactoryService.getService(databaseService).executeQuery(query);
        return output;
    }

    public Map<String, Object> getApprovalDetail(String approvalId) throws Exception {
        String query = String.format("SELECT * FROM \"INDO_CMS\".PUBLIC.INDO_CMS_APPROVAL_HEADER WHERE ROW_ID = '%s'", approvalId);
        Map<String, Object> approvalData = DatabaseFactoryService.getService(databaseService).executeQuery(query).get(0);
        String approvalDataBeforeString = approvalData.get("approval_data_before") != null ? approvalData.get("approval_data_before").toString() : "{}";
        String approvalDataAfterString = approvalData.get("approval_data_after") != null ? approvalData.get("approval_data_after").toString() : "{}";

        HashMap<String,Object> approvalDataBefore = new ObjectMapper().readValue(approvalDataBeforeString, HashMap.class);
        HashMap<String,Object> approvalDataAfter = new ObjectMapper().readValue(approvalDataAfterString, HashMap.class);

        String templateCode = approvalData.get("template_code").toString();

        String templateDetailFilter = " AND CASE WHEN is_edit = '1' THEN '1' WHEN is_primary = '1' THEN '1' ELSE '0' END = '1'";
        List<Map<String, Object>> templateDetail = templateService.getTemplateDetail(templateCode, templateDetailFilter);

        Map<String, Object> output = new HashMap<>();
        output.put("approval_data_before", approvalDataBefore);
        output.put("approval_data_after", approvalDataAfter);
        output.put("template_detail", templateDetail);
        // System.out.println("getApprovalDetail : templateDetail : " + templateDetail);

        return output;
    }

    public String getApprovalDownloadDetail(String approvalId) throws Exception {
        String output = null;
        String query = String.format("SELECT * FROM \"INDO_CMS\".PUBLIC.INDO_CMS_APPROVAL_HEADER WHERE ROW_ID = '%s'", approvalId);
        Map<String, Object> approvalData = DatabaseFactoryService.getService(databaseService).executeQuery(query).get(0);
        output = approvalData.get("approval_data_import").toString();
        return output;
    }

    public void approvalProcess(String approvalStatus, String approvalId, String payload) throws Exception {        
        boolean isFinalApproval = false;
        StringBuilder approvalDetailQuery = new StringBuilder("INSERT INTO \"INDO_CMS\".PUBLIC.INDO_CMS_APPROVAL_DETAIL");
        approvalDetailQuery.append(" (APPROVAL_HEADER_ID, APPROVAL_STATUS, APPROVAL_USERNAME, APPROVAL_ROLE, APPROVAL_DATE, APPROVAL_MESSAGE)");
        approvalDetailQuery.append(" VALUES ");
        approvalDetailQuery.append("(%s,%s,%s,%s,%s,%s)");

        approvalId = "'" + approvalId + "'";        
        String currentUser = "'" + authService.getCurrentUser() + "'";
        String currentUserRole = "'" + authService.getCurrentUserRole() + "'";
        String approvalDate = "'" + generalService.convertDateToString(new Date(), approvalPatternDate) + "'";
        String approvalMessage = "NULL";
        if (approvalStatus.equalsIgnoreCase("REJECTED")) {
            Map<String, Object> payloadMap = new ObjectMapper().readValue(payload, HashMap.class);
            approvalMessage = "'" + payloadMap.get("message").toString() + "'";
            isFinalApproval = true;
        }
        approvalStatus = "'" + approvalStatus + "'";
        String approvalDetailQueryFinal = String.format(approvalDetailQuery.toString(), approvalId, approvalStatus, currentUser, currentUserRole, approvalDate, approvalMessage);
        // System.out.println("approvalDetailQueryFinal : " + approvalDetailQueryFinal);

        DatabaseFactoryService.getService(databaseService).executeUpdate(approvalDetailQueryFinal);        

        // IF APPROVAL ACHIEVE CONDITION, THEN SET APPROVAL STATUS        
        String approvalQuery = null;
        if (approvalStatus.contains("APPROVED")) {            
            switch(approvalType) {
                case "SINGLE_USER_APPROVAL": case "SINGLE_ROLE_APPROVAL" : {
                    approvalQuery = String.format("SELECT * FROM \"INDO_CMS\".PUBLIC.INDO_CMS_APPROVAL_HEADER WHERE row_id = %s", approvalId);
                    // System.out.println("approvalQuery : " + approvalQuery);
                    Map<String, Object> approvalHeaderData = DatabaseFactoryService.getService(databaseService).executeQuery(approvalQuery).get(0);

                    String templateCode = approvalHeaderData.get("template_code").toString();
                    Map<String, Object> templateHeader = templateService.getTemplateHeader(templateCode);

                    String approvalType = approvalHeaderData.get("approval_type").toString();

                    switch (approvalType) {
                        case "IMPORT": {
                            String importFilePath = approvalHeaderData.get("approval_data_import").toString();
                            String importFileExtension = generalService.getFileExtension(importFilePath).toLowerCase();
                            switch(importFileExtension.toLowerCase()) {
                                case "csv":{                
                                    importService.importCSV(templateCode, importFilePath);
                                    break;
                                }
                                case "xls":case "xlsx": {
                                    importService.importExcel(templateCode, importFilePath);
                                    break;
                                }
                            }
                            break;
                        }
                        default:{
                            approvalQuery = approvalHeaderData.get("approval_query").toString();
                            templateHeader.put("query", approvalQuery);
                            System.out.println("approvalQuery : " + approvalQuery);
        
                            DatabaseFactoryService.getService(databaseService).executeUpdate(templateHeader);
                            break;
                        }
                    }

                    isFinalApproval = true;                    
                    break;
                }
            }
        }

        if (isFinalApproval) {
            approvalQuery = String.format("UPDATE \"INDO_CMS\".PUBLIC.INDO_CMS_APPROVAL_HEADER SET APPROVAL_STATUS = %s WHERE ROW_ID = %s", approvalStatus, approvalId);
            DatabaseFactoryService.getService(databaseService).executeUpdate(approvalQuery);
        }                

    }
}