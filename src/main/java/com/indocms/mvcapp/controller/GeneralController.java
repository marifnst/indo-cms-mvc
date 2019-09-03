package com.indocms.mvcapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GeneralController {
    
    @GetMapping("/general/error")
    public ModelAndView error500Controller() {
        return new ModelAndView("pages/500");
    }
}