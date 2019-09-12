package com.indocms.mvcapp.service;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ExportService {
    @Value("${app.database.service}")
    private String databaseService;

    @Value("${app.export.pattern.date}")
    private String exportPatternDate;

    @Value("${app.export.csv.column.delimiter}")
    private String csvColumnDelimiter;

    @Value("${app.export.csv.row.separator}")
    private String csvRowSeparator;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private GeneralService generalService;

    public void exportService(String exportType, String templateCode) throws Exception {
        switch(exportType.toLowerCase()) {
            case "csv":{
                exportToCSV(templateCode);
                break;
            }
            case "xls":case "xlsx": {
                break;
            }
        }
    }

    public String exportToCSV(String templateCode) throws Exception {
        String exportPath;
        BufferedWriter bufferedWriter = null;
        try {
            String dateFileName = generalService.convertDateToString(new Date(), exportPatternDate);
            exportPath = templateCode + "_" + dateFileName + ".csv";
            bufferedWriter = new BufferedWriter(new FileWriter(exportPath));

            Map<String, Object> templateHeader = templateService.getTemplateHeader(templateCode);

            String templateDetailFilter = " AND is_show = '1'";
            List<Map<String, Object>> templateDetail = templateService.getTemplateDetail(templateCode, templateDetailFilter);

            String databaseName = templateHeader.get("database_name").toString().trim();
            String databaseTableDelimiter = templateHeader.get("database_table_delimiter").toString().trim();
            String tableName = templateHeader.get("table_name").toString().trim();

            StringBuilder query = new StringBuilder("SELECT ");

            // generate csv header
            for (int i =0;i < templateDetail.size();i++) {
                String webColumn = templateDetail.get(i).get("web_column").toString();
                bufferedWriter.write(webColumn);

                String databaseColumn = templateDetail.get(i).get("database_column").toString();
                String queryColumn = templateDetail.get(i).get("query_column").toString();
                query.append(queryColumn).append(" AS ").append(databaseColumn);

                if (i + 1 < templateDetail.size()) {
                    bufferedWriter.write(csvColumnDelimiter);
                    query.append(", ");
                }
            }
            bufferedWriter.write(csvRowSeparator);

            query.append(" FROM ").append(databaseName).append(databaseTableDelimiter).append(tableName);
            templateHeader.put("query", query);
            System.out.println("query export csv : " + query);

            List<Map<String, Object>> rows = DatabaseFactoryService.getService(databaseService).executeQuery(templateHeader);

            // generate data
            for (Map<String, Object> row : rows) {
                for (int i = 0;i <  templateDetail.size();i++) {
                    String databaseColumn = templateDetail.get(i).get("database_column").toString();
                    String databaseValue = row.get(databaseColumn) != null ? row.get(databaseColumn).toString() : "";

                    bufferedWriter.write(databaseValue);

                    if (i + 1 < templateDetail.size()) {
                        bufferedWriter.write(csvColumnDelimiter);
                    }
                }
                bufferedWriter.write(csvRowSeparator);
            }
        } finally {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        }
        return exportPath;
    }

    public String exportToExcel(String exportType, String templateCode) throws Exception {
        String exportPath = null;
        Workbook wb = null;

        try {
            switch (exportType.toLowerCase()) {
                case "xls":{
                    wb = new HSSFWorkbook();
                    break;
                }
                case "xlsx":{
                    wb = new XSSFWorkbook();
                    break;
                }
            }
            Sheet sheet1 = wb.createSheet("Sheet1");
            Row row = null;
            Cell cell = null;

            Map<String, Object> templateHeader = templateService.getTemplateHeader(templateCode);

            String templateDetailFilter = " AND is_show = '1'";
            List<Map<String, Object>> templateDetail = templateService.getTemplateDetail(templateCode, templateDetailFilter);

            String databaseName = templateHeader.get("database_name").toString().trim();
            String databaseTableDelimiter = templateHeader.get("database_table_delimiter").toString().trim();
            String tableName = templateHeader.get("table_name").toString().trim();

            StringBuilder query = new StringBuilder("SELECT ");

            int rowCount = 0;

            // generate excel header
            row = sheet1.createRow(rowCount);
            for (int i =0;i < templateDetail.size();i++) {
                String webColumn = templateDetail.get(i).get("web_column").toString();                
                cell = row.createCell(i);
                cell.setCellValue(webColumn);

                String databaseColumn = templateDetail.get(i).get("database_column").toString();
                String queryColumn = templateDetail.get(i).get("query_column").toString();
                query.append(queryColumn).append(" AS ").append(databaseColumn);

                if (i + 1 < templateDetail.size()) {
                    query.append(", ");
                }
            }
            rowCount++;

            query.append(" FROM ").append(databaseName).append(databaseTableDelimiter).append(tableName);
            templateHeader.put("query", query);
            System.out.println("query export excel : " + query);

            List<Map<String, Object>> rows = DatabaseFactoryService.getService(databaseService).executeQuery(templateHeader);

            for (Map<String, Object> rowData : rows) {
                row = sheet1.createRow(rowCount);
                for (int i =0;i < templateDetail.size();i++) {
                    cell = row.createCell(i);

                    String databaseColumn = templateDetail.get(i).get("database_column").toString();
                    String databaseValue = rowData.get(databaseColumn) != null ? rowData.get(databaseColumn).toString() : "";
                    cell.setCellValue(databaseValue);
                }
                rowCount++;
            }
    
            String dateFileName = generalService.convertDateToString(new Date(), exportPatternDate);
            exportPath = templateCode + "_" + dateFileName + "." + exportType.toLowerCase();
            OutputStream fileOut = new FileOutputStream(exportPath);
            wb.write(fileOut);
        } finally {
            if (wb != null) {
                wb.close();
            }
        }
        return exportPath;
    }
}