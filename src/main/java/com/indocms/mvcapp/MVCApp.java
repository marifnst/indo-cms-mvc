package com.indocms.mvcapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MVCApp {

    public static void main(String[] args) {
        SpringApplication.run(MVCApp.class, args);
    }

}
