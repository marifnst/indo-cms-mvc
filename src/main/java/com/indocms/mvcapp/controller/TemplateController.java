package com.indocms.mvcapp.controller;

import java.util.Map;

import com.indocms.mvcapp.service.TemplateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TemplateController {
    @Value("${app.view.url.template.prefix}")
    private String viewUrlTemplatePrefix;

    @Value("${app.create.url.template.prefix}")
    private String createUrlTemplatePrefix;

    @Value("${app.export.url.template.prefix}")
    private String exportUrlTemplatePrefix;

    @Value("${app.import.url.template.prefix}")
    private String importUrlTemplatePrefix;

    @Value("${app.create.form.url.template.prefix}")
    private String createFormUrlTemplatePrefix;

    @Autowired
    private TemplateService templateService;

    @PreAuthorize("hasPermission('template/view/' + #templateCode, 'view')")
    @RequestMapping(value = "/template/view/{templateCode}")
    public ModelAndView viewTemplate(@PathVariable String templateCode) {
        ModelAndView output = new ModelAndView();
        try {
            Map<String, Object> outputService = templateService.viewService(templateCode);
            output.addObject("payload", outputService);
            // output.addObject("url_create", "template/create/" + templateCode);
            // output.addObject("url_import", "template/import/" + templateCode);
            // output.addObject("url_export", "template/export/" + templateCode);
            output.addObject("url_create", createUrlTemplatePrefix + templateCode);
            output.addObject("url_import", importUrlTemplatePrefix + templateCode);
            output.addObject("url_export", exportUrlTemplatePrefix + templateCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        output.setViewName("pages/template_table");
        return output;
    }

    @PreAuthorize("hasPermission('template/view/' + #templateCode, 'insert')")
    @RequestMapping(value = "/template/create/{templateCode}")
    public ModelAndView createTemplate(@PathVariable String templateCode, Model model) {
        ModelAndView output = new ModelAndView();
        try {
            Map<String, Object> outputService = templateService.createService(templateCode, viewUrlTemplatePrefix + templateCode);
            output.addObject("payload", outputService);
            output.addObject("url", "template/create/process/" + templateCode);
            // output.addObject("url_cancel", "template/view/" + templateCode);
            output.addObject("url_cancel", viewUrlTemplatePrefix + templateCode);
        } catch (Exception e) {
            e.printStackTrace();
        }        
        output.setViewName("pages/template_form");
        return output;
    }

    @PreAuthorize("hasPermission('template/form/create/' + #templateCode, 'view')")
    @RequestMapping(value = "/template/form/create/{templateCode}")
    public ModelAndView formCreateTemplate(@PathVariable String templateCode, Model model) {
        ModelAndView output = new ModelAndView();
        try {
            Map<String, Object> outputService = templateService.createService(templateCode, createFormUrlTemplatePrefix + templateCode);
            output.addObject("payload", outputService);
            output.addObject("url", "template/form/create/process/" + templateCode);
            // output.addObject("url_cancel", "template/view/" + templateCode);
            output.addObject("url_cancel", createFormUrlTemplatePrefix + templateCode);
        } catch (Exception e) {
            e.printStackTrace();
        }        
        output.setViewName("pages/template_form");
        return output;
    }

    @PreAuthorize("hasPermission('template/view/' + #templateCode, 'update')")
    @RequestMapping(value = "/template/edit/{templateCode}/{dataId}")
    public ModelAndView editTemplate(@PathVariable String templateCode, @PathVariable String dataId, String  model) {
        ModelAndView output = new ModelAndView();
        try {
            Map<String, Object> outputService = templateService.editService(templateCode, dataId, viewUrlTemplatePrefix + templateCode);
            output.addObject("payload", outputService);
            output.addObject("url", "template/edit/process/" + templateCode + "/" + dataId);
        } catch (Exception e) {
            e.printStackTrace();
        }        
        output.setViewName("pages/template_form");
        return output;
    }
}