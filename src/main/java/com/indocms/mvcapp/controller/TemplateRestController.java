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
            output.put("url", "template/view/" + templateCode);
        } catch (Exception e) {
            String stringStacktrace = generalService.getStringStacktrace(e);
            output.put("status", "Failed");
            output.put("message", stringStacktrace);
            output.put("url", "general/error");
        }
        return output;
    }

    @RequestMapping(value = "/template/edit/process/{templateCode}/{dataId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map<String, String> editProcess(@PathVariable String templateCode, @PathVariable String dataId, @RequestBody String payload) {
        Map<String, String> output = new HashMap<>();
        try {
            templateService.editProcessService(templateCode, dataId, payload);
            output.put("status", "Success");
            output.put("message", "Edit Data Success");
            output.put("url", "template/view/" + templateCode);
        } catch (Exception e) {
            String stringStacktrace = generalService.getStringStacktrace(e);
            output.put("status", "Failed");
            output.put("message", stringStacktrace);
            output.put("url", "general/error");
        }
        return output;
    }

    @RequestMapping(value = "/template/delete/process/{templateCode}/{dataId}", method = RequestMethod.POST)
    public Map<String, String> deleteProcess(@PathVariable String templateCode, @PathVariable String dataId) {
        Map<String, String> output = new HashMap<>();
        try {
            System.out.println("deleteProcess : " + templateCode + " : " + dataId);
            output.put("status", "Success");
            output.put("message", "Delete Data Success");
            output.put("url", "template/view/" + templateCode);
        } catch (Exception e) {
            String stringStacktrace = generalService.getStringStacktrace(e);
            output.put("status", "Failed");
            output.put("message", stringStacktrace);
            output.put("url", "general/error");
        }
        return output;
    }
}