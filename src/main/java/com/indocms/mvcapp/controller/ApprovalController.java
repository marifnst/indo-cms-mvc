package com.indocms.mvcapp.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import com.indocms.mvcapp.service.ApprovalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;

    @PreAuthorize("hasPermission('approval/list', 'view')")
    @GetMapping("/approval/list")
    public ModelAndView approvalList() {
        ModelAndView output = new ModelAndView("pages/approval_list");
        try {
            List<Map<String, Object>> approvalTaskList = approvalService.getApprovalTaskList();
            output.addObject("approval_task_list", approvalTaskList);
            output.addObject("detail_url", "/approval/detail/");
            output.addObject("download_detail_url", "/approval/download/detail/");
            output.addObject("approve_url", "/approval/approve/");
            output.addObject("reject_url", "/approval/reject/");
        } catch (Exception e) {
            e.printStackTrace();            
        }        
        return output;
    }

    @PostMapping("/approval/detail/{approvalId}")
    public ModelAndView approvalDetail(@PathVariable String approvalId) {
        ModelAndView output = new ModelAndView("pages/approval_detail");
        try {
            System.out.println("approval detail : " + approvalId);            
            output.addObject("payload", approvalService.getApprovalDetail(approvalId));
        } catch (Exception e) {
            e.printStackTrace();
        }        
        return output;
    }

    @PostMapping("/approval/download/detail/{approvalId}")
    public ResponseEntity<Object> approvalDownloadDetail(@PathVariable String approvalId) throws Exception {
        String exportOutputPath = approvalService.getApprovalDownloadDetail(approvalId);

        File file = new File(exportOutputPath);
        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("filename", file.getName());

        return ResponseEntity.ok()
            .headers(headers)
            .contentLength(file.length())
            .contentType(MediaType.parseMediaType("application/octet-stream"))
            .body(resource);
    }
}