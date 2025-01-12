package com.securefileshare.file_service.util;

import com.securefileshare.auth_service.security.JwtTokenProvider;
import com.securefileshare.file_service.model.Collection;
import com.securefileshare.file_service.repository.CollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccessValidator {

//    @Autowired
//    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CollectionRepository collectionRepository;

    public void validateUserAccess(String token, String collectionId) throws Exception {
        // String userId = jwtTokenProvider.getUserIdFromToken(token);
        String userId = null;
        // Check if the collection exists
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new Exception("Collection not found"));

        // Validate if the user is the owner or has shared access
        if (!collection.getOwnerId().equals(userId) && !collection.getSharedWith().contains(userId)) {
            throw new Exception("User does not have access to this collection");
        }
    }
} 