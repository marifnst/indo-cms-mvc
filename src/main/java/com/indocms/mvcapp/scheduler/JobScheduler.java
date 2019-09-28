package com.indocms.mvcapp.scheduler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.indocms.mvcapp.service.DatabaseFactoryService;
import com.indocms.mvcapp.service.GeneralService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobScheduler {
    @Value("${app.database.service}")
    private String databaseService;

    @Value("${app.approval.pattern.date}")
    private String approvalPatternDate;

    @Autowired
    private GeneralService generalService;

    @Scheduled(fixedRateString="${app.job.scheduler.interval}")
    public void jobScheduler() {
        try {
            String query = "SELECT * FROM INDO_CMS_JOB_HISTORY WHERE JOB_STATUS = 'In Progress' ORDER BY JOB_START_DATE";
            List<Map<String, Object>> jobList = DatabaseFactoryService.getService(databaseService).executeQuery(query);
            for (Map<String, Object> job : jobList) {
                String jobStatus = "Success";
                String jobMessage = "Execution Job Success";
                try {
                    String jobId = job.get("job_id").toString();
                    query = String.format("SELECT * FROM INDO_CMS_JOB_DETAIL WHERE JOB_ID = '%s' ORDER BY JOB_SEQUENCE", jobId);
                    List<Map<String, Object>> jobQueryList = DatabaseFactoryService.getService(databaseService).executeQuery(query);
                    for (Map<String, Object> jobQuery : jobQueryList) {
                        query = jobQuery.get("job_query").toString();
                        jobQuery.put("query", query);
                        DatabaseFactoryService.getService(databaseService).executeQuery(jobQuery);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    jobStatus = "Failed";
                    jobMessage = generalService.getStringStacktrace(e);
                }

                String jobRowId = job.get("row_id").toString();
                String jobEndDate = generalService.convertDateToString(new Date(), approvalPatternDate);
                query = String.format("UPDATE INDO_CMS_JOB_HISTORY SET JOB_STATUS = '%s', JOB_MESSAGE = '%s', JOB_END_DATE = '%s' WHERE ROW_ID = '%s'", jobStatus, jobMessage, jobEndDate, jobRowId);
                DatabaseFactoryService.getService(databaseService).executeUpdate(query);
                System.gc();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.gc();
        }
    }
}