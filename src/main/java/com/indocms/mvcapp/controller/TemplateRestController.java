package com.indocms.mvcapp.controller;

import java.util.HashMap;
import java.util.Map;

import com.indocms.mvcapp.service.GeneralService;
import com.indocms.mvcapp.service.TemplateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TemplateRestController {

    @Autowired
    private TemplateService templateService;

    @Autowired
    private GeneralService generalService;

    @RequestMapping(value = "/template/create/process/{templateCode}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map<String, String> createProcess(@PathVariable String templateCode, @RequestBody String payload) {
        Map<String, String> output = new HashMap<>();
        try {
            templateService.createProcessService(templateCode, payload);
            output.put("status", "Success");
            output.put("message", "Insert Data Success");
            output.put("url", "template/view/TEMPLATE_CODE");
        } catch (Exception e) {
            String stringStacktrace = generalService.getStringStacktrace(e);
            output.put("status", "Failed");
            output.put("message", stringStacktrace);
            output.put("url", "general/error");
        }
        return output;
    }
}