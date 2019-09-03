package com.indocms.mvcapp.service;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.stereotype.Service;

@Service
public class GeneralService {
    public String getStringStacktrace(Exception exception) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String output = sw.toString(); // stack trace as a string
        return output;
    }
}