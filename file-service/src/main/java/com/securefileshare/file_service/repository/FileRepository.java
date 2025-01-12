package com.securefileshare.file_service.repository;

import com.securefileshare.file_service.model.File;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends MongoRepository<File, String> {
    List<File> findByCollectionId(String collectionId);
}