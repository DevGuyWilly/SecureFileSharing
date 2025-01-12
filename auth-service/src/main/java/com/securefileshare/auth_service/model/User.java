package com.securefileshare.auth_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Document(collection = "users")
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String password;

    private String role;

    private boolean active = true;

    private Instant createdAt;

    // Total storage allocated (in bytes)
    private long storageQuota;      

    // Current storage used (in bytes)
    private long storageUsed;

    // List of collection IDs owned by user
    private List<String> collections; 

    // Collection ID -> Set of user IDs with access
    private Map<String, Set<String>> sharedAccess; 
    // Recent file/collection activities
    private List<String> recentActivity; 

    // User preferences (e.g., default collection, notification settings)
    // {
    //     "defaultCollection": "personal-files",
    //     "notifyOnShare": "true",
    //     "notifyOnUpload": "false",
    //     "displayMode": "grid",
    //     "defaultSort": "name_asc",
    //     "language": "en",
    //     "timezone": "UTC-5",
    //     "emailNotifications": "daily"
    // } -- What I have in Mind.
    private Map<String, String> preferences;  

    // Explicit getters and setters
    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public String getRole() {

        return role;
    }

    public void setRole(String role) {

        this.role = role;
    }

    public boolean isActive() {

        return active;
    }

    public void setActive(boolean active) {

        this.active = active;
    }

    public void setCreatedAt(Instant createdAt) {

        this.createdAt = createdAt;
    }

    // New getters and setters
    public long getStorageQuota() {
        return storageQuota;
    }

    public void setStorageQuota(long storageQuota) {
        this.storageQuota = storageQuota;
    }

    public long getStorageUsed() {
        return storageUsed;
    }

    public void setStorageUsed(long storageUsed) {
        this.storageUsed = storageUsed;
    }

    public List<String> getCollections() {
        return collections;
    }

    public void setCollections(List<String> collections) {
        this.collections = collections;
    }

    public Map<String, Set<String>> getSharedAccess() {
        return sharedAccess;
    }

    public void setSharedAccess(Map<String, Set<String>> sharedAccess) {
        this.sharedAccess = sharedAccess;
    }

    // Helper methods for storage management
    public boolean hasStorageSpace(long fileSize) {
        return (storageUsed + fileSize) <= storageQuota;
    }

    public void addStorageUsed(long size) {
        this.storageUsed += size;
    }

    public void removeStorageUsed(long size) {
        this.storageUsed = Math.max(0, this.storageUsed - size);
    }
}
