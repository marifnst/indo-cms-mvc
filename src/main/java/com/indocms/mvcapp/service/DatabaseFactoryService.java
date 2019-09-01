package com.indocms.mvcapp.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.indocms.mvcapp.database.Database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseFactoryService {
    @Autowired
    private List<Database> databaseList;

    private static final Map<String, Database> myServiceCache = new HashMap<>();

    @PostConstruct
    public void initMyServiceCache() {
        for (Database service : databaseList) {
            // System.out.println("service.getType() : " + service.getType());
            myServiceCache.put(service.getType(), service);
        }
    }

    public static Database getService(String type) {
        // System.out.println("myServiceCache + " + myServiceCache);
        Database service = myServiceCache.get(type);
        if (service == null) {
            throw new RuntimeException("Unknown service type: " + type);
        }            
        return service;
    }
}