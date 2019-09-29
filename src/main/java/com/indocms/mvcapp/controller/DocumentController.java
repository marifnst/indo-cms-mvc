package com.indocms.mvcapp.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.indocms.mvcapp.service.DocumentService;
import com.indocms.mvcapp.service.GeneralService;
import com.indocms.mvcapp.service.MenuService;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DocumentController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private GeneralService generalService;

    @PreAuthorize("hasPermission('document/view', 'view')")
    @GetMapping("/document/view")
    public ModelAndView viewDocument() {
        ModelAndView output = new ModelAndView("pages/document_list");
        try {
            Map<String, Object> menuPermission = menuService.getSessionMenu("document/view");
            List<Map<String, Object>> documentList = documentService.viewDocument();
            output.addObject("menu", menuPermission);
            output.addObject("document_list", documentList);
            output.addObject("url_refresh", "/document/view");
            output.addObject("url_create", "/document/form/create");
            output.addObject("url_edit", "/document/form/edit/");
            output.addObject("url_delete", "/document/form/delete/process/");
            output.addObject("url_download", "/document/download/");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    @PreAuthorize("hasPermission('document/view', 'insert')")
    @GetMapping("/document/form/create")
    public ModelAndView createDocumentForm() {
        ModelAndView output = new ModelAndView("pages/document_form");
        try {
            Map<String, Object> menuPermission = menuService.getSessionMenu("document/view");
            output.addObject("menu", menuPermission);
            output.addObject("url", "/document/form/create/process");
            output.addObject("url_cancel", "/document/view");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    @PreAuthorize("hasPermission('document/view', 'insert')")
    @PostMapping("/document/form/create/process")
    @ResponseBody
    public Map<String, String> createProcessDocument(
        @RequestParam(name = "document_name") String documentName,
        @RequestParam(name = "document_description") String documentDescription,
        @RequestParam(name = "document_file") MultipartFile documentFile
    ) {
        Map<String, String> output = new HashMap<>();
        try {
            documentService.createDocumentProcess(documentName, documentDescription, documentFile);
            output.put("status", "Success");
            output.put("message", "Insert New Document Success");
        } catch (Exception e) {
            e.printStackTrace();
            output.put("status", "Failed");
            output.put("message", generalService.getStringStacktrace(e));
        }
        return output;
    }

    @PreAuthorize("hasPermission('document/view', 'update')")
    @GetMapping("/document/form/edit/{rowId}")
    public ModelAndView editDocumentForm(@PathVariable String rowId) {
        ModelAndView output = new ModelAndView("pages/document_form");
        try {
            Map<String, Object> documentData = documentService.getDocumentDataById(rowId);
            Map<String, Object> menuPermission = menuService.getSessionMenu("document/view");
            output.addObject("menu", menuPermission);
            output.addObject("document", documentData);
            output.addObject("url", "/document/form/edit/process/" + rowId);
            output.addObject("url_cancel", "/document/view");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    @PreAuthorize("hasPermission('document/view', 'update')")
    @PostMapping("/document/form/edit/process/{rowId}")
    @ResponseBody
    public Map<String, String> editProcessDocument(
        @PathVariable String rowId,
        @RequestParam(name = "document_name") String documentName,
        @RequestParam(name = "document_description") String documentDescription,
        @RequestParam(name = "document_file") MultipartFile documentFile
    ) {
        Map<String, String> output = new HashMap<>();
        try {
            documentService.editDocumentProcess(rowId, documentName, documentDescription, documentFile);
            output.put("status", "Success");
            output.put("message", "Update Document (With Id " + rowId + ") Success");
            output.put("url", "/document/view");
        } catch (Exception e) {
            e.printStackTrace();
            output.put("status", "Failed");
            output.put("message", generalService.getStringStacktrace(e));
        }
        return output;
    }

    @PreAuthorize("hasPermission('document/view', 'delete')")
    @PostMapping("/document/form/delete/process/{rowId}")
    @ResponseBody
    public Map<String, String> deleteProcessDocument(@PathVariable String rowId) {
        Map<String, String> output = new HashMap<>();
        try {
            documentService.deleteDocumentProcess(rowId);
            output.put("status", "Success");
            output.put("message", "Delete Document (With Id " + rowId + ") Success");
            output.put("url", "/document/view");
        } catch (Exception e) {
            e.printStackTrace();
            output.put("status", "Failed");
            output.put("message", generalService.getStringStacktrace(e));
        }
        return output;
    }

    @PreAuthorize("hasPermission('document/view', 'export')")
    @PostMapping("/document/download/{rowId}")
    @ResponseBody
    public ResponseEntity<Object> downloadProcess(@PathVariable String rowId) throws Exception {
        String exportOutputPath = documentService.getDocumentDataById(rowId).get("document_path").toString();

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