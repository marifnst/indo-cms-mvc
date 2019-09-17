package com.indocms.mvcapp.controller;

import java.util.List;
import java.util.Map;

import com.indocms.mvcapp.service.ApprovalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;

    @PreAuthorize("hasPermission('approval/list', 'view')")
    @GetMapping("approval/list")
    public ModelAndView approvalList() {
        ModelAndView output = new ModelAndView("pages/approval_list");
        try {
            List<Map<String, Object>> approvalTaskList = approvalService.getApprovalTaskList();
            output.addObject("approval_task_list", approvalTaskList);
        } catch (Exception e) {
            e.printStackTrace();            
        }        
        return output;
    }
}