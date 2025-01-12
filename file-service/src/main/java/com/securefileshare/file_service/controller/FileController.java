package com.securefileshare.file_service.controller;

import com.securefileshare.file_service.model.File;
import com.securefileshare.file_service.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("collectionId") String collectionId,
            @RequestHeader("Authorization") String token) {
        try {
            File uploadedFile = fileService.uploadFile(file, collectionId, token);
            return ResponseEntity.ok(uploadedFile);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<?> getFile(
            @PathVariable String fileId,
            @RequestHeader("Authorization") String token) {
        try {
            Resource resource = fileService.getFile(fileId, token);
            File fileMetadata = fileService.getFileMetadata(fileId, token);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fileMetadata.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + fileMetadata.getName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/metadata/{fileId}")
    public ResponseEntity<?> getFileMetadata(
            @PathVariable String fileId,
            @RequestHeader("Authorization") String token) {
        try {
            File file = fileService.getFileMetadata(fileId, token);
            return ResponseEntity.ok(file);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(
            @PathVariable String fileId,
            @RequestHeader("Authorization") String token) {
        try {
            fileService.deleteFile(fileId, token);
            return ResponseEntity.ok(Map.of("message", "File deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/collection/{collectionId}")
    public ResponseEntity<?> getFilesInCollection(
            @PathVariable String collectionId,
            @RequestHeader("Authorization") String token) {
        try {
            List<File> files = fileService.getFilesInCollection(collectionId, token);
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
} 