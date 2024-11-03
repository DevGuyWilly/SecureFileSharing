# Secure File-Sharing Platform

A full-stack application for secure file management and sharing with end-to-end encryption. Built with an MVC pattern, a Microservice Architecture, and a layered data flow with encryption to prioritize data privacy and scalability.

## Table of Contents
- [Project Overview](#project-overview)
- [Tech Stack](#tech-stack)
- [Architecture Overview](#architecture-overview)
- [Design Patterns](#design-patterns)
- [Microservices](#microservices)
- [Data Flow and Security](#data-flow-and-security)
- [Installation and Setup](#installation-and-setup)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Future Improvements](#future-improvements)
- [License](#license)

## Project Overview

This platform allows users to securely upload, download, and share files with advanced encryption using the AES-256 encryption standard. It uses AWS for cloud storage and features a layered data flow with end-to-end encryption to ensure data security.

## Tech Stack

- **Frontend**: React, TypeScript
- **Backend**: Spring Boot (Java), MVC Pattern, Microservice Architecture
- **Database**: MongoDB (File metadata), AWS S3 (File storage)
- **Encryption**: AES-256 Encryption
- **Cloud Provider**: AWS

## Architecture Overview

This project is built using the Microservice Architecture. Key services include:
1. **Auth Service**: Manages user authentication, role-based access control, and token validation.
2. **File Service**: Handles file upload, download, encryption, and secure sharing.
3. **Notification Service**: Sends user notifications for file-related events.
4. **Activity Logging Service**: Logs user actions for auditing and security.

### Design Patterns
- **MVC Pattern**: Organizes code into Models, Views, and Controllers to separate concerns.
- **Factory Pattern**: Used for creating encryption and decryption instances.
- **Decorator Pattern**: Adds security layers (e.g., authorization checks) around file handling functions.
- **Facade Pattern**: Simplifies interactions with AWS services (S3, IAM, etc.).
- **Observer Pattern**: Used to handle activity logging and notifications.
- **Strategy Pattern**: Allows flexibility to switch encryption algorithms in the future if needed.

### Microservices

#### 1. Auth Service
- **Endpoints**:
  - `/register`: User registration
  - `/login`: User login
  - `/logout`: User logout
  - `/validate-token`: Validate user token and permissions
- **Data**: Manages user information, authentication tokens, and role permissions.

#### 2. File Service
- **Endpoints**:
  - `/upload`: Upload files securely with encryption
  - `/download/{fileId}`: Download encrypted files after decryption
  - `/share`: Share files with unique, expirable links
  - `/revoke-share`: Revoke file-sharing permissions
- **Data**: Metadata for files (file ID, name, owner, permissions) stored in MongoDB; encrypted files stored in AWS S3.

#### 3. Notification Service
- **Endpoints**:
  - `/notify`: Send notifications to users for file actions (e.g., file sharing)
  - `/get-notifications`: Retrieve user notifications
- **Data**: Notification details (event type, user, file link, etc.).

#### 4. Activity Logging Service
- **Endpoints**:
  - `/log-activity`: Log user actions related to file management
  - `/get-logs`: Retrieve activity logs for auditing
- **Data**: Logs include actions like file uploads, downloads, deletions, and shares.

### Data Flow and Security

#### Overall System Data Flow
flowchart TD
    User --> AuthService["Auth Service"]
    AuthService -->|JWT Token| User
    User -->|Upload/Download| FileService["File Service"]
    FileService -->|Encrypt| AWS_S3["AWS S3 Storage"]
    FileService -->|Store Metadata| MongoDB["MongoDB Database"]
    FileService --> ActivityLoggingService["Activity Logging Service"]
    FileService --> NotificationService["Notification Service"]
    ActivityLoggingService --> MongoDB
    NotificationService --> User

####  File Upload Flow
sequenceDiagram
    participant User
    participant AuthService
    participant FileService
    participant AWS_S3 as AWS S3
    participant MongoDB

    User ->> AuthService: Authenticate
    AuthService -->> User: JWT Token
    User ->> FileService: Upload File with JWT
    FileService ->> FileService: Encrypt File (AES-256)
    FileService ->> AWS_S3: Store Encrypted File
    FileService ->> MongoDB: Save Metadata (File ID, User ID, Encryption Key)
    FileService ->> ActivityLoggingService: Log Upload Activity

#### File Download Flow
sequenceDiagram
    participant User
    participant AuthService
    participant FileService
    participant AWS_S3 as AWS S3
    participant MongoDB

    User ->> AuthService: Authenticate
    AuthService -->> User: JWT Token
    User ->> FileService: Download File with JWT
    FileService ->> MongoDB: Fetch File Metadata
    FileService ->> AWS_S3: Retrieve Encrypted File
    FileService ->> FileService: Decrypt File
    FileService -->> User: Send Decrypted File
    FileService ->> ActivityLoggingService: Log Download Activity


#### File Sharing Flow
sequenceDiagram
    participant User
    participant AuthService
    participant FileService
    participant MongoDB
    participant NotificationService

    User ->> AuthService: Authenticate
    AuthService -->> User: JWT Token
    User ->> FileService: Request File Share with JWT
    FileService ->> MongoDB: Generate Secure Link and Save Expiry
    FileService ->> NotificationService: Notify Recipient
    NotificationService -->> Recipient: Share Link Notification
    FileService ->> ActivityLoggingService: Log Sharing Activity


