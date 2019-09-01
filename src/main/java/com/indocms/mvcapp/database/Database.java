package com.indocms.mvcapp.database;

import java.util.List;
import java.util.Map;

public interface Database {
    public void executeUpdate(String query) throws Exception;
    public List<Map<String, Object>> executeQuery(String query) throws Exception;
    public List<Map<String, Object>> executeQuery(Map<String, Object> databaseProperty) throws Exception;
    public String getType();
}