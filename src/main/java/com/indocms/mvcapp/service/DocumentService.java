package com.indocms.mvcapp.service;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentService {
    @Value("${app.database.service}")
    private String databaseService;

    @Value("${app.export.pattern.date}")
    private String exportPatternDate;

    @Value("${app.approval.pattern.date}")
    private String approvalPatternDate;

    @Autowired
    private AuthService authService;

    @Autowired
    private GeneralService generalService;

    public List<Map<String, Object>> viewDocument() throws Exception {
        String query = "SELECT * FROM INDO_CMS_DOCUMENT";
        System.out.println("viewDocument : " + query);
        List<Map<String, Object>> documentList = DatabaseFactoryService.getService(databaseService).executeQuery(query);
        return documentList;
    }

    public Map<String, Object> getDocumentDataById(String rowId) throws Exception {
        String query = String.format("SELECT * FROM INDO_CMS_DOCUMENT WHERE ROW_ID = '%s'", rowId);
        System.out.println("getDocumentDataById : " + query);
        Map<String, Object> documentData = DatabaseFactoryService.getService(databaseService).executeQuery(query).get(0);
        return documentData;
    }

    public void createDocumentProcess(String documentName, String documentDescription, MultipartFile documentFile) throws Exception {
        InputStream documentFileInputStream = documentFile.getInputStream();
        String documentFileName = generalService.convertDateToString(new Date(), exportPatternDate) + "_" + documentFile.getOriginalFilename();

        // sementara disamakan terlebih dahulu path nya
        String documentFilePath = documentFileName;

        String documentFileExtension = generalService.getFileExtension(documentFileName);

        Path exportPath = Paths.get(documentFilePath);
        Files.copy(documentFileInputStream, exportPath, StandardCopyOption.REPLACE_EXISTING);        
        IOUtils.closeQuietly(documentFileInputStream);

        File destinationFile = new File(documentFileName);
        String documentFileSize = generalService.getFileSizeInKB(destinationFile);
        String documentCreatedBy = authService.getCurrentUser();
        String documentCreatedDate = generalService.convertDateToString(new Date(), approvalPatternDate);

        StringBuilder query = new StringBuilder("INSERT INTO INDO_CMS_DOCUMENT ");
        query.append("(DOCUMENT_NAME, DOCUMENT_DESCRIPTION, DOCUMENT_EXT_TYPE, DOCUMENT_SIZE, DOCUMENT_PATH, DOCUMENT_CREATED_BY, DOCUMENT_CREATED_DATE) ");
        query.append(" VALUES ('%s','%s','%s','%s','%s','%s','%s')");

        String queryFinal = String.format(query.toString(), documentName, documentDescription, documentFileExtension, documentFileSize, documentFilePath, documentCreatedBy, documentCreatedDate);
        System.out.println("createDocumentProcess : " + queryFinal);

        DatabaseFactoryService.getService(databaseService).executeUpdate(queryFinal);
    }

    public void editDocumentProcess(String rowId, String documentName, String documentDescription, MultipartFile documentFile) throws Exception {
        InputStream documentFileInputStream = documentFile.getInputStream();
        String documentFileName = generalService.convertDateToString(new Date(), exportPatternDate) + "_" + documentFile.getOriginalFilename();

        // sementara disamakan terlebih dahulu path nya
        String documentFilePath = documentFileName;

        String documentFileExtension = generalService.getFileExtension(documentFileName);

        Path exportPath = Paths.get(documentFilePath);
        Files.copy(documentFileInputStream, exportPath, StandardCopyOption.REPLACE_EXISTING);        
        IOUtils.closeQuietly(documentFileInputStream);

        File destinationFile = new File(documentFileName);
        String documentFileSize = generalService.getFileSizeInKB(destinationFile);
        String documentModifiedBy = authService.getCurrentUser();
        String documentModifiedDate = generalService.convertDateToString(new Date(), approvalPatternDate);

        StringBuilder query = new StringBuilder("UPDATE INDO_CMS_DOCUMENT SET");
        query
            .append(" DOCUMENT_NAME = '%s',")
            .append(" DOCUMENT_DESCRIPTION = '%s',")
            .append(" DOCUMENT_EXT_TYPE = '%s',")
            .append(" DOCUMENT_SIZE = '%s',")
            .append(" DOCUMENT_PATH = '%s',")
            .append(" DOCUMENT_MODIFIED_BY = '%s',")
            .append(" DOCUMENT_MODIFIED_DATE = '%s'")
            .append(" WHERE ROW_ID = '%s'");

        String queryFinal = String.format(query.toString(), documentName, documentDescription, documentFileExtension, documentFileSize, documentFilePath, documentModifiedBy, documentModifiedDate, rowId);
        System.out.println("editDocumentProcess : " + queryFinal);

        DatabaseFactoryService.getService(databaseService).executeUpdate(queryFinal);
    }

    public void deleteDocumentProcess(String rowId) throws Exception {
        Map<String, Object> documentData = this.getDocumentDataById(rowId);

        String documentPath = documentData.get("document_path").toString();
        File documentFile = new File(documentPath);
        if (documentFile.exists()) {
            documentFile.delete();
        }

        String query = String.format("DELETE FROM INDO_CMS_DOCUMENT WHERE ROW_ID = '%s'", rowId);
        System.out.println("deleteDocumentProcess : " + query);

        DatabaseFactoryService.getService(databaseService).executeUpdate(query);
    }
}