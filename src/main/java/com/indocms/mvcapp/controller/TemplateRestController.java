package com.indocms.mvcapp.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.indocms.mvcapp.service.GeneralService;
import com.indocms.mvcapp.service.TemplateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class TemplateRestController {

    @Value("${app.view.url.template.prefix}")
    private String viewUrlTemplatePrefix;

    @Value("${app.create.form.url.template.prefix}")
    private String createFormUrlTemplatePrefix;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private GeneralService generalService;

    @RequestMapping(value = "/encrypt")
    public String encrypt(@RequestParam String word) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();        
        return passwordEncoder.encode(word);
    }

    @PreAuthorize("hasPermission('template/view/' + #templateCode, 'insert')")
    @RequestMapping(value = "/template/create/process/{templateCode}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map<String, String> createProcess(@PathVariable String templateCode, @RequestBody String payload) {
        Map<String, String> output = new HashMap<>();
        try {
            output = templateService.createProcessService(viewUrlTemplatePrefix + templateCode, templateCode, payload);
            // output.put("status", "Success");
            // output.put("message", "Insert Data Success");
            // output.put("url", "template/view/" + templateCode);
            output.put("url", viewUrlTemplatePrefix + templateCode);
        } catch (Exception e) {
            e.printStackTrace();
            String stringStacktrace = generalService.getStringStacktrace(e);
            output.put("status", "Failed");
            output.put("message", stringStacktrace);
            output.put("url", "general/error");
        }
        return output;
    }

    @PreAuthorize("hasPermission('template/form/create/' + #templateCode, 'insert')")
    @RequestMapping(value = "/template/form/create/process/{templateCode}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map<String, String> formCreateProcess(@PathVariable String templateCode, @RequestBody String payload) {
        Map<String, String> output = new HashMap<>();
        try {
            output = templateService.createProcessService(createFormUrlTemplatePrefix + templateCode, templateCode, payload);
            // output.put("status", "Success");
            // output.put("message", "Insert Data Success");
            // output.put("url", "template/view/" + templateCode);
            output.put("url", viewUrlTemplatePrefix + templateCode);
        } catch (Exception e) {
            e.printStackTrace();
            String stringStacktrace = generalService.getStringStacktrace(e);
            output.put("status", "Failed");
            output.put("message", stringStacktrace);
            output.put("url", "general/error");
        }
        return output;
    }

    @PreAuthorize("hasPermission('template/view/' + #templateCode, 'update')")
    @RequestMapping(value = "/template/edit/process/{templateCode}/{dataId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map<String, String> editProcess(@PathVariable String templateCode, @PathVariable String dataId, @RequestBody String payload) {
        Map<String, String> output = new HashMap<>();
        try {
            output = templateService.editProcessService(viewUrlTemplatePrefix + templateCode, templateCode, dataId, payload);
            // output.put("status", "Success");
            // output.put("message", "Edit Data Success");
            // output.put("url", "template/view/" + templateCode);
            output.put("url", viewUrlTemplatePrefix + templateCode);
        } catch (Exception e) {
            e.printStackTrace();
            String stringStacktrace = generalService.getStringStacktrace(e);
            output.put("status", "Failed");
            output.put("message", stringStacktrace);
            output.put("url", "general/error");
        }
        return output;
    }

    @PreAuthorize("hasPermission('template/view/' + #templateCode, 'delete')")
    @RequestMapping(value = "/template/delete/process/{templateCode}/{dataId}", method = RequestMethod.POST)
    public Map<String, String> deleteProcess(@PathVariable String templateCode, @PathVariable String dataId) {
        Map<String, String> output = new HashMap<>();
        try {
            // System.out.println("deleteProcess : " + templateCode + " : " + dataId);
            output = templateService.deleteProcessService(viewUrlTemplatePrefix + templateCode, templateCode, dataId);
            // output.put("status", "Success");
            // output.put("message", "Delete Data Success");
            // output.put("url", "template/view/" + templateCode);
            output.put("url", viewUrlTemplatePrefix + templateCode);
        } catch (Exception e) {
            e.printStackTrace();
            String stringStacktrace = generalService.getStringStacktrace(e);
            output.put("status", "Failed");
            output.put("message", stringStacktrace);
            output.put("url", "general/error");
        }
        return output;
    }

    @PreAuthorize("hasPermission('template/view/' + #templateCode, 'import')")
    @PostMapping(value = "/template/import/{templateCode}")
    public Map<String, String> importProcess(@PathVariable String templateCode, @RequestParam("file") MultipartFile file) {
        Map<String, String> output = new HashMap<>();
        try {
            System.out.println("importProcess : " + templateCode + " : " + file.getOriginalFilename());
            output = templateService.importProcessService(viewUrlTemplatePrefix + templateCode, templateCode, file);
            // output.put("status", "Success");
            // output.put("message", "Import Data Success");
            // output.put("url", "template/view/" + templateCode);
            output.put("url", viewUrlTemplatePrefix + templateCode);
        } catch (Exception e) {
            e.printStackTrace();
            String stringStacktrace = generalService.getStringStacktrace(e);
            output.put("status", "Failed");
            output.put("message", stringStacktrace);
            output.put("url", "general/error");
        }
        return output;
    }

    @PreAuthorize("hasPermission('template/view/' + #templateCode, 'export')")
    @PostMapping(value = "/template/export/{templateCode}")
    public ResponseEntity<Object> exportProcess(@PathVariable String templateCode, @RequestBody (required = false) String payload) throws Exception {
        System.out.println("payload : " + payload);
        String exportOutputPath = templateService.exportProcessService(templateCode, payload);

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