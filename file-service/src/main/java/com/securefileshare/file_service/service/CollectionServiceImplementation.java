package com.securefileshare.file_service.service;

import com.securefileshare.file_service.model.Collection;
import com.securefileshare.file_service.repository.CollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class CollectionServiceImplementation implements CollectionService {

    @Autowired
    private CollectionRepository collectionRepository;

    @Override
    public Collection createCollection(String name, String token) throws Exception {
        // Validate user access here using token

        Collection collection = new Collection();
        collection.setName(name);
        collection.setOwnerId("userIdFromToken"); // Extract user ID from token

        return collectionRepository.save(collection);
    }

    @Override
    public Collection getCollection(String collectionId, String token) throws Exception {
        // Validate user access here using token

        return collectionRepository.findById(collectionId)
                .orElseThrow(() -> new Exception("Collection not found"));
    }

    @Override
    public List<Collection> getUserCollections(String token) throws Exception {
        // Validate user access here using token

        String userId = "userIdFromToken"; // Extract user ID from token
        return collectionRepository.findByOwnerId(userId);
    }

    @Override
    public Collection shareCollection(String collectionId, Set<String> userIds, String token) throws Exception {
        // Validate user access here using token

        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new Exception("Collection not found"));

        for (String userId : userIds) {
            collection.addSharedUser(userId);
        }

        return collectionRepository.save(collection);
    }

    @Override
    public Collection revokeAccess(String collectionId, Set<String> userIds, String token) throws Exception {
        // Validate user access here using token

        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new Exception("Collection not found"));

        for (String userId : userIds) {
            collection.removeSharedUser(userId);
        }

        return collectionRepository.save(collection);
    }

    @Override
    public void deleteCollection(String collectionId, String token) throws Exception {
        // Validate user access here using token

        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new Exception("Collection not found"));

        collectionRepository.delete(collection);
    }
}