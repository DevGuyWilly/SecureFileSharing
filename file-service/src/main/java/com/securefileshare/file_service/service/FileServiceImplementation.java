package com.securefileshare.file_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.securefileshare.file_service.config.S3Config;
import com.securefileshare.file_service.model.File;
import com.securefileshare.file_service.repository.FileRepository;
import com.securefileshare.file_service.repository.CollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class FileServiceImplementation implements FileService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private AmazonS3 s3client;

    @Autowired
    private S3Config s3Config;

    @Override
    public File uploadFile(MultipartFile file, String collectionId, String token) throws Exception {
        // Validate user and collection access here using token

        // Create file metadata
        File fileMetadata = new File();
        fileMetadata.setName(file.getOriginalFilename());
        fileMetadata.setContentType(file.getContentType());
        fileMetadata.setSize(file.getSize());
        fileMetadata.setCollectionId(collectionId);
        //GET BACK TO THIS LATER
        fileMetadata.setOwnerId("userIdFromToken"); // Extract user ID from token

        // Upload file to S3
        String s3Key = "files/" + fileMetadata.getName(); // Define S3 key
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            s3client.putObject(s3Config.getBucketName(), s3Key, inputStream, metadata);
        }

        fileMetadata.setS3Key(s3Key);
        return fileRepository.save(fileMetadata);
    }

    @Override
    public Resource getFile(String fileId, String token) throws Exception {
        // Validate user access here using token

        File fileMetadata = fileRepository.findById(fileId)
                .orElseThrow(() -> new Exception("File not found"));

        // Download file from S3
        S3Object s3Object = s3client.getObject(s3Config.getBucketName(), fileMetadata.getS3Key());
        InputStream inputStream = s3Object.getObjectContent();
        Path tempFile = Files.createTempFile(fileMetadata.getName(), ".tmp");
        Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);

        return new UrlResource(tempFile.toUri());
    }

    @Override
    public File getFileMetadata(String fileId, String token) throws Exception {
        // Validate user access here using token

        return fileRepository.findById(fileId)
                .orElseThrow(() -> new Exception("File not found"));
    }

    @Override
    public void deleteFile(String fileId, String token) throws Exception {
        // Validate user access here using token

        File fileMetadata = fileRepository.findById(fileId)
                .orElseThrow(() -> new Exception("File not found"));

        // Delete file from S3
        s3client.deleteObject(s3Config.getBucketName(), fileMetadata.getS3Key());

        // Remove metadata from MongoDB
        fileRepository.delete(fileMetadata);
    }

    @Override
    public List<File> getFilesInCollection(String collectionId, String token) throws Exception {
        // Validate user access here using token

        return fileRepository.findByCollectionId(collectionId);
    }
}