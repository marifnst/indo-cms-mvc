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
public class JobService {

    @Value("${app.database.service}")
    private String databaseService;

    @Value("${app.approval.pattern.date}")
    private String approvalPatternDate;
    
    @Autowired
    private AuthService authService;

    @Autowired
    private GeneralService generalService;

    public List<Map<String, Object>> getRunningJob() throws Exception {
        StringBuilder query = new StringBuilder("SELECT * FROM INDO_CMS_JOB_HISTORY WHERE JOB_STATUS = 'In Progress'");

        List<Map<String, Object>> jobList = DatabaseFactoryService.getService(databaseService).executeQuery(query.toString());
        return jobList;
    }
    
    public List<Map<String, Object>> getJobList() throws Exception {
        StringBuilder query = new StringBuilder("SELECT A.ROW_ID, A.JOB_ID, A.JOB_DESCRIPTION, B.MAX_ROW_ID, C.JOB_STATUS, C.JOB_START_BY, C.JOB_START_DATE, C.JOB_END_DATE");
        query.append(" FROM INDO_CMS_JOB_HEADER A");
        query.append(" LEFT JOIN");
        query.append(" (SELECT JOB_ID, MAX(ROW_ID) MAX_ROW_ID FROM INDO_CMS_JOB_HISTORY GROUP BY JOB_ID ) B ON A.JOB_ID = B.JOB_ID");
        query.append(" LEFT JOIN INDO_CMS_JOB_HISTORY C ON B.MAX_ROW_ID = C.ROW_ID");

        List<Map<String, Object>> jobList = DatabaseFactoryService.getService(databaseService).executeQuery(query.toString());
        return jobList;
    }

    public void executeJob(String payload) throws Exception {
        HashMap<String,Object> payloadMap = new ObjectMapper().readValue(payload, HashMap.class);
        List<String> jobList = (List<String>) payloadMap.get("data");
        for (String job : jobList) {
            StringBuilder queryInsert = new StringBuilder("INSERT INTO INDO_CMS_JOB_HISTORY (JOB_ID, JOB_STATUS, JOB_START_BY, JOB_START_DATE)");
            queryInsert.append(" VALUES (%s, %s, %s, %s)");

            String jobStatus = "'In Progress'";
            String jobStartBy = "'" + authService.getCurrentUser() + "'";
            String jobStartDate = "'" + generalService.convertDateToString(new Date(), approvalPatternDate) + "'";
            String queryInsertFinal = String.format(queryInsert.toString(), "'" + job + "'", jobStatus, jobStartBy, jobStartDate);

            System.out.println("executeJob : queryInsertFinal : " + queryInsertFinal);

            DatabaseFactoryService.getService(databaseService).executeUpdate(queryInsertFinal);
        }
    }

    public String getMessage(String jobId) throws Exception {
        String output = null;
        String query = String.format("SELECT * FROM INDO_CMS_JOB_HISTORY WHERE ROW_ID = (SELECT MAX(ROW_ID) FROM INDO_CMS_JOB_HISTORY WHERE JOB_ID = '%s')", jobId);
        Map<String, Object> queryResult = DatabaseFactoryService.getService(databaseService).executeQuery(query).get(0);
        if (queryResult.get("job_message") != null) {
            output = queryResult.get("job_message").toString();
        } else {
            output = "No Execution Message";
        }        
        return output;
    }
}