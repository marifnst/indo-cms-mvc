package com.indocms.mvcapp.controller;

import java.util.HashMap;
import java.util.Map;

import com.indocms.mvcapp.service.ApprovalService;
import com.indocms.mvcapp.service.GeneralService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApprovalRestController {
    @Autowired
    private ApprovalService approvalService;

    @Autowired
    private GeneralService generalService;

    @RequestMapping(value = "/approval/approve/{approvalId}", method = RequestMethod.POST)
    public Map<String, Object> approveProcess(@PathVariable String approvalId) {
        Map<String, Object> output = new HashMap<>();
        try {
            System.out.println("approveProcess : payload : " + approvalId);
            approvalService.approvalProcess("APPROVED", approvalId, "{}");
            output.put("status", "Success");
            output.put("message", "Data Has Been Approved Successfully");
            output.put("url", "approval/list");
        } catch (Exception e) {
            e.printStackTrace();
            String stringStacktrace = generalService.getStringStacktrace(e);
            output.put("status", "Failed");
            output.put("message", stringStacktrace);
            output.put("url", "general/error");
        }        
        return output;
    }

    @RequestMapping(value = "/approval/reject/{approvalId}", method = RequestMethod.POST)
    public Map<String, Object> rejectProcess(@PathVariable String approvalId, @RequestBody String payload) {
        Map<String, Object> output = new HashMap<>();
        try {
            System.out.println("rejectProcess : payload : " + approvalId + " : " + payload);
            approvalService.approvalProcess("REJECTED", approvalId, payload);
            output.put("status", "Success");
            output.put("message", "Data Has Been Rejected Successfully");
            output.put("url", "approval/list");
        } catch (Exception e) {
            e.printStackTrace();
            String stringStacktrace = generalService.getStringStacktrace(e);
            output.put("status", "Failed");
            output.put("message", stringStacktrace);
            output.put("url", "general/error");
        }        
        return output;
    }
}