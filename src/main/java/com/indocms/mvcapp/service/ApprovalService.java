package com.indocms.mvcapp.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ApprovalService {

    @Value("${app.database.service}")
    private String databaseService;

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
        String createdDate = generalService.convertDateToString(new Date(), "yyyy-MM-dd hh:mm:ss");
        String dataBefore = payloadBefore;
        String dataAfter = payload;
        // String dataImport = "NULL";
        String queryFinal = query.toString();
        queryFinal = String.format(queryFinal, approvalPendingFlag, approvalType, templateCode, createdBy, createdDate, dataBefore, dataAfter, dataImport, crudQuery.replaceAll("'", "''"));
        System.out.println("crudApprovalnjection : " + queryFinal);
        
        DatabaseFactoryService.getService(databaseService).executeUpdate(queryFinal);
    }
}