package com.indocms.mvcapp.database;

import java.util.List;
import java.util.Map;

public interface Database {
    public List<Map<String, Object>> executeUpdate(String query) throws Exception;
    public List<Map<String, Object>> executeUpdate(Map<String, Object> databaseProperty) throws Exception;
    public Map<String, Object> executeUpdateImport(String query) throws Exception;
    public Map<String, Object> executeUpdateImport(Map<String, Object> databaseProperty) throws Exception;
    public List<Map<String, Object>> executeQuery(String query) throws Exception;
    public List<Map<String, Object>> executeQuery(Map<String, Object> databaseProperty) throws Exception;
    public String getType();
}