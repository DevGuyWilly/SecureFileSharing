package com.securefileshare.file_service.repository;

import com.securefileshare.file_service.model.Collection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionRepository extends MongoRepository<Collection, String> {
    List<Collection> findByOwnerId(String ownerId);
}