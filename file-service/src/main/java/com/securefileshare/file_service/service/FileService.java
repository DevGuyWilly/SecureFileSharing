package com.securefileshare.file_service.service;

import com.securefileshare.file_service.model.File;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    File uploadFile(MultipartFile file, String collectionId, String token) throws Exception;
    Resource getFile(String fileId, String token) throws Exception;
    File getFileMetadata(String fileId, String token) throws Exception;
    void deleteFile(String fileId, String token) throws Exception;
    List<File> getFilesInCollection(String collectionId, String token) throws Exception;
}