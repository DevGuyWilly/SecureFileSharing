package com.securefileshare.file_service.controller;

import com.securefileshare.file_service.model.Collection;
import com.securefileshare.file_service.service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/collections")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    @PostMapping
    public ResponseEntity<?> createCollection(
            @RequestBody Map<String, String> request,
            @RequestHeader("Authorization") String token) {
        try {
            Collection collection = collectionService.createCollection(
                request.get("name"), token);
            return ResponseEntity.ok(collection);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{collectionId}")
    public ResponseEntity<?> getCollection(
            @PathVariable String collectionId,
            @RequestHeader("Authorization") String token) {
        try {
            Collection collection = collectionService.getCollection(collectionId, token);
            return ResponseEntity.ok(collection);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getUserCollections(
            @RequestHeader("Authorization") String token) {
        try {
            List<Collection> collections = collectionService.getUserCollections(token);
            return ResponseEntity.ok(collections);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{collectionId}/share")
    public ResponseEntity<?> shareCollection(
            @PathVariable String collectionId,
            @RequestBody Set<String> userIds,
            @RequestHeader("Authorization") String token) {
        try {
            Collection collection = collectionService.shareCollection(
                collectionId, userIds, token);
            return ResponseEntity.ok(collection);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{collectionId}/share")
    public ResponseEntity<?> revokeAccess(
            @PathVariable String collectionId,
            @RequestBody Set<String> userIds,
            @RequestHeader("Authorization") String token) {
        try {
            Collection collection = collectionService.revokeAccess(
                collectionId, userIds, token);
            return ResponseEntity.ok(collection);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{collectionId}")
    public ResponseEntity<?> deleteCollection(
            @PathVariable String collectionId,
            @RequestHeader("Authorization") String token) {
        try {
            collectionService.deleteCollection(collectionId, token);
            return ResponseEntity.ok(Map.of("message", "Collection deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
} 