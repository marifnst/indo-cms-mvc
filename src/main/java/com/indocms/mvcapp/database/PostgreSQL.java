package com.indocms.mvcapp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.indocms.mvcapp.service.GeneralService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class PostgreSQL implements Database {

    @Autowired
    private Environment environment;

    @Autowired
    private GeneralService generalService;

    @Override
    public List<Map<String, Object>> executeUpdate(String query) throws Exception {
        Connection connection = null;
        List<Map<String, Object>> output = new ArrayList<>();

        try {
            String className = environment.getProperty("app.database.classname");
            String connectionString = environment.getProperty("app.database.connection.string");
            String username = environment.getProperty("app.database.username");
            String password = environment.getProperty("app.database.password");
    
            // Class.forName("org.postgresql.Driver");
            // Connection connection = DriverManager.getConnection("jdbc:postgresql://172.24.0.2:5432/INDO_CMS", "postgres", "password");
            Class.forName(className);
            connection = DriverManager.getConnection(connectionString, username, password);
    
            PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1;i <= resultSetMetaData.getColumnCount();i++) {
                    String columnLabel = resultSetMetaData.getColumnLabel(i);
                    row.put(columnLabel, resultSet.getObject(i));
                }
                output.add(row);
            }
        } finally {
            if (connection != null) {
                connection.close();
            }            
        }
        return output;
    }

    @Override
    public List<Map<String, Object>> executeUpdate(Map<String, Object> databaseProperty) throws Exception {
        Connection connection = null;
        List<Map<String, Object>> output = new ArrayList<>();

        try {
            String className = databaseProperty.get("database_classname").toString().trim();
            String connectionString = databaseProperty.get("database_connection_string").toString().trim();
            String username = databaseProperty.get("database_username").toString().trim();
            String password = databaseProperty.get("database_password").toString().trim();
            String query = databaseProperty.get("query").toString().trim();
    
            // Class.forName("org.postgresql.Driver");
            // Connection connection = DriverManager.getConnection("jdbc:postgresql://172.24.0.2:5432/INDO_CMS", "postgres", "password");
            Class.forName(className);
            connection = DriverManager.getConnection(connectionString, username, password);
    
            PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1;i <= resultSetMetaData.getColumnCount();i++) {
                    String columnLabel = resultSetMetaData.getColumnLabel(i);
                    row.put(columnLabel, resultSet.getObject(i));
                }
                output.add(row);
            }                   
        } finally {
            if (connection != null) {
                connection.close();
            }            
        }
        return output;
    }

    @Override
    public Map<String, Object> executeUpdateImport(String query) throws Exception {
        Connection connection = null;
        Map<String, Object> output = new HashMap<>();

        try {
            String className = environment.getProperty("app.database.classname");
            String connectionString = environment.getProperty("app.database.connection.string");
            String username = environment.getProperty("app.database.username");
            String password = environment.getProperty("app.database.password");
    
            // Class.forName("org.postgresql.Driver");
            // Connection connection = DriverManager.getConnection("jdbc:postgresql://172.24.0.2:5432/INDO_CMS", "postgres", "password");
            Class.forName(className);
            connection = DriverManager.getConnection(connectionString, username, password);
    
            PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1;i <= resultSetMetaData.getColumnCount();i++) {
                    String columnLabel = resultSetMetaData.getColumnLabel(i);
                    row.put(columnLabel, resultSet.getObject(i));
                }
                // output.add(row);
                output.put("generated_key", row);
            }
            output.put("status", "Success");
            output.put("message", "Execute Update Success");
        } catch (Exception e) {
            output.put("status", "Failed");
            output.put("message", "Execute Update Failed");
            output.put("stacktrace", generalService.getStringStacktrace(e));
        } finally {
            if (connection != null) {
                connection.close();
            }            
        }
        return output;
    }

    @Override
    public Map<String, Object> executeUpdateImport(Map<String, Object> databaseProperty) throws Exception {
        Connection connection = null;
        Map<String, Object> output = new HashMap<>();

        try {
            String className = databaseProperty.get("database_classname").toString().trim();
            String connectionString = databaseProperty.get("database_connection_string").toString().trim();
            String username = databaseProperty.get("database_username").toString().trim();
            String password = databaseProperty.get("database_password").toString().trim();
            String query = databaseProperty.get("query").toString().trim();
    
            // Class.forName("org.postgresql.Driver");
            // Connection connection = DriverManager.getConnection("jdbc:postgresql://172.24.0.2:5432/INDO_CMS", "postgres", "password");
            Class.forName(className);
            connection = DriverManager.getConnection(connectionString, username, password);
    
            PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1;i <= resultSetMetaData.getColumnCount();i++) {
                    String columnLabel = resultSetMetaData.getColumnLabel(i);
                    row.put(columnLabel, resultSet.getObject(i));
                }
                // output.add(row);
                output.put("generated_key", row);
            }
            output.put("status", "Success");
            output.put("message", "Execute Update Success");
        } catch (Exception e) {
            output.put("status", "Failed");
            output.put("message", "Execute Update Failed");
            output.put("stacktrace", generalService.getStringStacktrace(e));
        } finally {
            if (connection != null) {
                connection.close();
            }            
        }
        return output;
    }

    @Override
    public List<Map<String, Object>> executeQuery(String query) throws Exception {
        Connection connection = null;
        List<Map<String, Object>> output = new ArrayList<>();

        try {
            String className = environment.getProperty("app.database.classname");
            String connectionString = environment.getProperty("app.database.connection.string");
            String username = environment.getProperty("app.database.username");
            String password = environment.getProperty("app.database.password");
    
            // Class.forName("org.postgresql.Driver");
            // Connection connection = DriverManager.getConnection("jdbc:postgresql://172.24.0.2:5432/INDO_CMS", "postgres", "password");
            Class.forName(className);
            connection = DriverManager.getConnection(connectionString, username, password);
    
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1;i <= resultSetMetaData.getColumnCount();i++) {
                    String columnLabel = resultSetMetaData.getColumnLabel(i);
                    row.put(columnLabel, resultSet.getObject(i));
                }
                output.add(row);
            }                   
        } finally {
            if (connection != null) {
                connection.close();
            }            
        }
        return output;
    }

    @Override
    public List<Map<String, Object>> executeQuery(Map<String, Object> databaseProperty) throws Exception {
        Connection connection = null;
        List<Map<String, Object>> output = new ArrayList<>();

        try {
            String className = databaseProperty.get("database_classname").toString().trim();
            String connectionString = databaseProperty.get("database_connection_string").toString().trim();
            String username = databaseProperty.get("database_username").toString().trim();
            String password = databaseProperty.get("database_password").toString().trim();
            String query = databaseProperty.get("query").toString().trim();
    
            // Class.forName("org.postgresql.Driver");
            // Connection connection = DriverManager.getConnection("jdbc:postgresql://172.24.0.2:5432/INDO_CMS", "postgres", "password");
            Class.forName(className);
            connection = DriverManager.getConnection(connectionString, username, password);
    
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1;i <= resultSetMetaData.getColumnCount();i++) {
                    String columnLabel = resultSetMetaData.getColumnLabel(i);
                    row.put(columnLabel, resultSet.getObject(i));
                }
                output.add(row);
            }                   
        } finally {
            if (connection != null) {
                connection.close();
            }            
        }
        return output;
    }

    @Override
    public String getType() {
        return PostgreSQL.class.getName();
    }

}