package com.indocms.mvcapp.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ImportService {
    @Value("${app.single.database}")
    private boolean isSingleDatabase;

    @Value("${app.database.service}")
    private String databaseService;

    @Value("${app.export.csv.column.delimiter}")
    private String csvColumnDelimiter;

    @Autowired
    private TemplateService templateService;

    private List<String> fileHeader = null;
    private Map<String, Map<String, Object>> templateDetailMap = null;
    private Map<String, Map<String, Object>> templateDetailWebMap = null;
    private Map<String, Object> primaryKey = null;

    //#region import csv
    public void importCSV(String templateCode, String filePath) throws Exception {
        BufferedReader bufferedReader = null;
        try {
            Map<String, Object> templateHeader = templateService.getTemplateHeader(templateCode);
            String databaseName = templateHeader.get("database_name").toString().trim();
            String databaseTableDelimiter = templateHeader.get("database_table_delimiter").toString().trim();
            String tableName = templateHeader.get("table_name").toString().trim();

            String templateDetailFilter = " AND is_show = '1'";
            List<Map<String, Object>> templateDetail = templateService.getTemplateDetail(templateCode, templateDetailFilter);
            this.templateDetailExtraction(templateDetail);

            bufferedReader = new BufferedReader(new FileReader(filePath));
            String row = bufferedReader.readLine();

            int rowCount = 0;
            fileHeader = new ArrayList<>();

            StringBuilder queryInsertColumn = new StringBuilder("INSERT INTO ");
            queryInsertColumn.append(databaseName).append(databaseTableDelimiter).append(tableName).append(" (");

            while (row != null) {
                String[] rowSplit = row.split("\\" + csvColumnDelimiter, -1);
                StringBuilder queryInsertValue = new StringBuilder();
                StringBuilder queryUpdate = new StringBuilder("UPDATE ").append(databaseName).append(databaseTableDelimiter).append(tableName).append(" SET ");
                Map<String, Object> queryExecResult = null;

                if (rowCount == 0) {                    
                    for (int i = 0;i < rowSplit.length;i++) {
                        String tmpColumn = rowSplit[i];
                        fileHeader.add(tmpColumn);
                        String tmpDatabaseColumn = templateDetailWebMap.get(tmpColumn).get("database_column").toString();

                        String tmpPrimaryKeyWebColumn = primaryKey.get("primary_web_column").toString();
                        if (!tmpColumn.equalsIgnoreCase(tmpPrimaryKeyWebColumn)) {
                            queryInsertColumn.append(tmpDatabaseColumn);

                            if (i + 1 < rowSplit.length) {
                                queryInsertColumn.append(", ");
                            }
                        }
                    }
                    queryInsertColumn.append(") VALUES (");
                } else {
                    for (int i = 0;i < rowSplit.length;i++) {
                        String tmpColumnValue = rowSplit[i];                        

                        // int tmpPrimaryIndex = (int) primaryKey.get("primary_index");
                        String tmpWebColumn = fileHeader.get(i);
                        String tmpDatabaseColumn = templateDetailWebMap.get(tmpWebColumn).get("database_column").toString();
                        String tmpPrimaryKeyColumn = primaryKey.get("primary_database_column").toString();

                        if (tmpDatabaseColumn.equalsIgnoreCase(tmpPrimaryKeyColumn) && !tmpColumnValue.equalsIgnoreCase("")) {
                            primaryKey.put("primary_key_value", tmpColumnValue);
                        } else if (!tmpDatabaseColumn.equalsIgnoreCase(tmpPrimaryKeyColumn)) {
                            queryInsertValue.append("'").append(tmpColumnValue).append("'");
                                                        
                            queryUpdate.append(tmpDatabaseColumn).append(" = '").append(tmpColumnValue).append("'");

                            if (i + 1 < rowSplit.length) {
                                queryInsertValue.append(", ");
                                queryUpdate.append(", ");
                            }
                        }
                    }

                    queryInsertValue.append(")");
                    
                    if (primaryKey.containsKey("primary_key_value")) {
                        String tmpPrimaryKeyColumn = primaryKey.get("primary_database_column").toString();
                        String tmpPrimaryKeyValue = primaryKey.get("primary_key_value").toString();
                        queryUpdate.append(" WHERE ").append(tmpPrimaryKeyColumn).append(" = '").append(tmpPrimaryKeyValue).append("'");
    
                        System.out.println("queryUpdate : " + queryUpdate);
                        templateHeader.put("query", queryUpdate);
    
                        // queryExecResult = DatabaseFactoryService.getService(databaseService).executeUpdateImport(templateHeader);
                        if (isSingleDatabase) {
                            queryExecResult = DatabaseFactoryService.getService(databaseService).executeUpdateImport(queryUpdate.toString());
                        } else {
                            queryExecResult = DatabaseFactoryService.getService(databaseService).executeUpdateImport(templateHeader);
                        }                        
                        primaryKey.remove("primary_key_value");
                    } else {
                        String finalInsertQuery = queryInsertColumn.toString() + queryInsertValue.toString();
                        templateHeader.put("query", finalInsertQuery);
    
                        System.out.println("finalInsertQuery : " + finalInsertQuery);
                        // queryExecResult = DatabaseFactoryService.getService(databaseService).executeUpdateImport(templateHeader);
                        if (isSingleDatabase) { 
                            queryExecResult = DatabaseFactoryService.getService(databaseService).executeUpdateImport(finalInsertQuery);
                        } else {
                            queryExecResult = DatabaseFactoryService.getService(databaseService).executeUpdateImport(templateHeader);
                        }
                    }
                }

                System.out.println("queryExecResult : " + queryExecResult);
                System.out.println("=======================================================");
                row = bufferedReader.readLine();
                rowCount++;
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
    }
    //#endregion import csv

    public void importExcel(String templateCode, String filePath) throws Exception {
        Workbook workbook = null;
        try {
            Map<String, Object> templateHeader = templateService.getTemplateHeader(templateCode);
            String databaseName = templateHeader.get("database_name").toString().trim();
            String databaseTableDelimiter = templateHeader.get("database_table_delimiter").toString().trim();
            String tableName = templateHeader.get("table_name").toString().trim();

            String templateDetailFilter = " AND is_show = '1'";
            List<Map<String, Object>> templateDetail = templateService.getTemplateDetail(templateCode, templateDetailFilter);
            this.templateDetailExtraction(templateDetail);

            InputStream inp = new FileInputStream(filePath);
            workbook = WorkbookFactory.create(inp);
            Sheet sheet = workbook.getSheetAt(0);
            Row row = null;
            Cell cell = null;

            fileHeader = new ArrayList<>();
            Map<String, Object> queryExecResult = null;
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                row = sheet.getRow(i);

                StringBuilder queryInsertColumn = new StringBuilder("INSERT INTO ").append(databaseName).append(databaseTableDelimiter).append(tableName).append(" (");
                StringBuilder queryInsertValue = new StringBuilder();
                StringBuilder queryUpdate = new StringBuilder("UPDATE ").append(databaseName).append(databaseTableDelimiter).append(tableName).append(" SET ");

                System.out.println("iterate row : " + i + " of " + sheet.getLastRowNum());
                for (int j = 0;j < row.getLastCellNum();j++) {
                    System.out.println("iterate cell : " + j + " of " + row.getLastCellNum());
                    cell = row.getCell(j);
                    String tmpWebColumn = null;
                    if (i == 0) {
                        tmpWebColumn = cell.getStringCellValue();
                        fileHeader.add(tmpWebColumn);
                        /*                                                
                        String tmpDatabaseColumn = templateDetailWebMap.get(tmpWebColumn).get("database_column").toString();

                        String tmpPrimaryKey = primaryKey.get("primary_web_column").toString();
                        if (!tmpWebColumn.equals(tmpPrimaryKey)) {
                            queryInsertColumn.append(tmpDatabaseColumn);
                        }

                        if (j + 1 < row.getLastCellNum()) {
                            queryInsertColumn.append(", ");
                        }*/
                    } else {
                        if (cell == null) {
                            System.out.println("column " + j + " : NULL");
                        } else {
                            // System.out.println("column " + j + " : " + cell.getStringCellValue());
                            tmpWebColumn = fileHeader.get(j);
                            String tmpDatabaseColumn = templateDetailWebMap.get(tmpWebColumn).get("database_column").toString();
                            String tmpPrimaryKeyColumn = primaryKey.get("primary_database_column").toString();
                            String tmpCellValue = null;

                            switch (cell.getCellType()) {
                                case NUMERIC:{
                                    String tmpDataType = templateDetailWebMap.get(tmpWebColumn).get("data_type").toString();
                                    if (tmpDataType.equalsIgnoreCase("integer")) {
                                        tmpCellValue = String.valueOf((int) cell.getNumericCellValue());
                                    } else {
                                        tmpCellValue = String.valueOf(cell.getNumericCellValue());
                                    }                                    
                                    break;
                                }
                                default:{
                                    tmpCellValue = cell.getStringCellValue();
                                    break;
                                }
                            }

                            if (tmpDatabaseColumn.equals(tmpPrimaryKeyColumn) && !tmpCellValue.equals("")) {
                                primaryKey.put("primary_key_value", tmpCellValue);
                            } else if (!tmpDatabaseColumn.equals(tmpPrimaryKeyColumn)){
                                queryInsertColumn.append(tmpDatabaseColumn);
                                queryInsertValue.append("'").append(tmpCellValue).append("'");

                                queryUpdate.append(tmpDatabaseColumn).append(" = '").append(tmpCellValue).append("'");
    
                                if (j + 1 < row.getLastCellNum()) {
                                    queryInsertColumn.append(", ");
                                    queryInsertValue.append(", ");
                                    queryUpdate.append(", ");
                                }
                            }
                        }                    
                    }
                }

                if (primaryKey.containsKey("primary_key_value")) {
                    String tmpPrimaryKeyColumn = primaryKey.get("primary_database_column").toString();
                    String tmpPrimaryKeyValue = primaryKey.get("primary_key_value").toString();
                    queryUpdate.append(" WHERE ").append(tmpPrimaryKeyColumn).append(" = '").append(tmpPrimaryKeyValue).append("'");
                    System.out.println(queryUpdate);
                    templateHeader.put("query", queryUpdate);

                    // queryExecResult = DatabaseFactoryService.getService(databaseService).executeUpdateImport(templateHeader);
                    if (isSingleDatabase) {
                        queryExecResult = DatabaseFactoryService.getService(databaseService).executeUpdateImport(queryUpdate.toString());
                    } else {
                        queryExecResult = DatabaseFactoryService.getService(databaseService).executeUpdateImport(templateHeader);
                    }
                    primaryKey.remove("primary_key_value");
                } else {
                    queryInsertColumn.append(") VALUES (");
                    queryInsertValue.append(")");
                    String finalInsertQuery = queryInsertColumn.toString() + queryInsertValue.toString();                    
                    templateHeader.put("query", finalInsertQuery);
                    System.out.println(finalInsertQuery);

                    // queryExecResult = DatabaseFactoryService.getService(databaseService).executeUpdateImport(templateHeader);
                    if (isSingleDatabase) {
                        queryExecResult = DatabaseFactoryService.getService(databaseService).executeUpdateImport(finalInsertQuery);
                    } else {
                        queryExecResult = DatabaseFactoryService.getService(databaseService).executeUpdateImport(templateHeader);
                    }
                }

                if (queryExecResult.get("status").toString().equalsIgnoreCase("failed")) {
                    System.out.println(queryExecResult.get("stacktrace").toString());
                }
                System.out.println("===========================");
            }
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
    }

    public void templateDetailExtraction(List<Map<String, Object>> templateDetail) {
        templateDetailMap = new HashMap<>();
        templateDetailWebMap = new HashMap<>();

        for (Map<String, Object> row : templateDetail) {
            String databaseColumn = row.get("database_column").toString();
            String webColumn = row.get("web_column").toString();
            String isPrimary = row.get("is_primary") != null ? row.get("is_primary").toString() : "0";

            templateDetailMap.put(databaseColumn, row);
            templateDetailWebMap.put(webColumn, row);

            if (isPrimary.equals("1")) {
                primaryKey = new HashMap<>();
                primaryKey.put("primary_database_column", databaseColumn);
                primaryKey.put("primary_web_column", webColumn);
            }
        }
    }
}