package com.indocms.mvcapp.controller;

import java.util.Map;

import com.indocms.mvcapp.service.TemplateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TemplateController {
    @Autowired
    private TemplateService templateService;

    @RequestMapping(value = "/template/view/{templateCode}")
    public ModelAndView viewTemplate(@PathVariable String templateCode) {
        ModelAndView output = new ModelAndView();
        try {
            Map<String, Object> outputService = templateService.viewService(templateCode);
            output.addObject("payload", outputService);
            output.addObject("url_import", "template/import/" + templateCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        output.setViewName("pages/template_table");
        return output;
    }

    @RequestMapping(value = "/template/create/{templateCode}")
    public ModelAndView createTemplate(@PathVariable String templateCode, Model model) {
        ModelAndView output = new ModelAndView();
        try {
            Map<String, Object> outputService = templateService.createService(templateCode);
            output.addObject("payload", outputService);
            output.addObject("url", "template/create/process/" + templateCode);            
        } catch (Exception e) {
            e.printStackTrace();
        }        
        output.setViewName("pages/template_form");
        return output;
    }

    @RequestMapping(value = "/template/edit/{templateCode}/{dataId}")
    public ModelAndView editTemplate(@PathVariable String templateCode, @PathVariable String dataId, String  model) {
        ModelAndView output = new ModelAndView();
        try {
            Map<String, Object> outputService = templateService.editService(templateCode, dataId);
            output.addObject("payload", outputService);
            output.addObject("url", "template/edit/process/" + templateCode + "/" + dataId);
        } catch (Exception e) {
            e.printStackTrace();
        }        
        output.setViewName("pages/template_form");
        return output;
    }
}