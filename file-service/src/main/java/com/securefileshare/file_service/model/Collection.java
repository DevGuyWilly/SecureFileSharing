package com.securefileshare.file_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "collections")
public class Collection {
    @Id
    private String id;
    
    private String name;
    private String ownerId;
    private Set<String> sharedWith;  // Set of user IDs with access
    private Instant createdAt;
    private Instant lastModified;
    private long totalSize;  // Total size of all files in collection

    // Constructor
    public Collection() {
        this.createdAt = Instant.now();
        this.lastModified = Instant.now();
        this.sharedWith = new HashSet<>();
        this.totalSize = 0L;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Set<String> getSharedWith() {
        return sharedWith;
    }

    public void setSharedWith(Set<String> sharedWith) {
        this.sharedWith = sharedWith;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastModified() {
        return lastModified;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    // Helper methods
    public void addSharedUser(String userId) {
        this.sharedWith.add(userId);
    }

    public void removeSharedUser(String userId) {
        this.sharedWith.remove(userId);
    }

    public boolean hasAccess(String userId) {
        return ownerId.equals(userId) || sharedWith.contains(userId);
    }

    public void updateSize(long deltaSize) {
        this.totalSize += deltaSize;
        this.lastModified = Instant.now();
    }
} 