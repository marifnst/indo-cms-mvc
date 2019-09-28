package com.indocms.mvcapp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.indocms.mvcapp.service.GeneralService;
import com.indocms.mvcapp.service.JobService;
import com.indocms.mvcapp.service.MenuService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class JobController {
    @Autowired
    private MenuService menuService;

    @Autowired
    private JobService jobService;

    @Autowired
    private GeneralService generalService;

    @PreAuthorize("hasPermission('job/view', 'view')")
    @GetMapping("job/view")
    public ModelAndView getJobList() {
        ModelAndView output = new ModelAndView("pages/template_job");
        try {
            List<Map<String, Object>> jobList = jobService.getJobList();
            Map<String, Object> menuPermission = menuService.getSessionMenu("job/view");

            output.addObject("jobs", jobList);
            output.addObject("menu", menuPermission);
            output.addObject("refresh_url", "job/view");
        } catch (Exception e) {
            e.printStackTrace();
        }        
        return output;
    }

    @PostMapping("job/execute")
    @ResponseBody
    public Map<String, String> executeJob(@RequestBody String payload) {
        Map<String, String> output = new HashMap<>();
        try {
            System.out.println("executeJob : payload : " + payload);
            jobService.executeJob(payload);
            output.put("status", "Success");
            output.put("message", "Execute Job Success");
            output.put("url", "job/view");
        } catch (Exception e) {
            e.printStackTrace();
            output.put("status", "Failed");
            output.put("message", e.getMessage());
            output.put("stacktrace", generalService.getStringStacktrace(e));
        }        
        return output;
    }

    @PostMapping("job/message/{jobId}")
    @ResponseBody
    public Map<String, String> jobMessage(@PathVariable String jobId) {
        Map<String, String> output = new HashMap<>();
        try {
            System.out.println("jobMessage : " + jobId);
            String message = jobService.getMessage(jobId);
            output.put("status", "Success");
            output.put("message", message);
        } catch (Exception e) {
            e.printStackTrace();
            output.put("status", "Failed");
            output.put("message", e.getMessage());
            output.put("stacktrace", generalService.getStringStacktrace(e));
        }        
        return output;
    }
}