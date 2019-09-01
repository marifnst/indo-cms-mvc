package com.indocms.mvcapp.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TemplateRestController {

    @RequestMapping(value = "/template/create/process/{templateCode}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void createProcess(@PathVariable String templateCode, @RequestBody String payload) {
        System.out.println(payload);
    }
}