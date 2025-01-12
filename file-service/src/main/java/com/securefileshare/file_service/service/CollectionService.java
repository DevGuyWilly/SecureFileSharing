package com.securefileshare.file_service.service;

import com.securefileshare.file_service.model.Collection;

import java.util.List;
import java.util.Set;

public interface CollectionService {
    Collection createCollection(String name, String token) throws Exception;
    Collection getCollection(String collectionId, String token) throws Exception;
    List<Collection> getUserCollections(String token) throws Exception;
    Collection shareCollection(String collectionId, Set<String> userIds, String token) throws Exception;
    Collection revokeAccess(String collectionId, Set<String> userIds, String token) throws Exception;
    void deleteCollection(String collectionId, String token) throws Exception;
}