# Restricted File Sharing System

## Overview
This is a secure, authenticated file-sharing system backend built with Spring Boot. It extends the concepts of custom authentication and secure file sharing to build a system where files are private by default and can only be shared with selected, verified users. This project is a backend-only implementation providing a robust REST API.

## Features

### 1. User Authentication
*   **Registration**: Users register with email and password.
*   **Email Verification**: Users receive a verification link (valid for 10 minutes) to activate their account.
*   **JWT Security**: Stateless authentication using JSON Web Tokens.
*   **Restriction**: Only verified users can log in and access the system.

### 2. File Management
*   **Secure Uploads**: Authenticated users can upload files. Files are stored securely on the server.
*   **Ownership**: The uploader retains ownership of the file.
*   **Persistence**: Files are stored permanently and are not deleted after download (unlike ephemeral file sharing systems).

### 3. Restricted Sharing
*   **Explicit Access**: Files are not public. They can only be downloaded by the owner or users explicitly granted access.
*   **User Validation**: Files can only be shared with users who are already registered and verified in the system.
*   **Unified Access**: Uses a unique download token for file access, but validates the identity of the requester before permitting the download.
*   **Notifications**: Recipients receive an email notification when a file is shared with them.

## Tech Stack
*   **Language**: Java 21
*   **Framework**: Spring Boot 3.4
*   **Security**: Spring Security, JWT (jjwt)
*   **Database**: H2 Database (File-based)
*   **Email**: Spring Boot Mail Starter (Gmail SMTP)
*   **Build Tool**: Gradle

## Getting Started

### Prerequisites
*   Java Development Kit (JDK) 21 or higher
*   Git

### Setup & Configuration

1.  **Clone the repository**
    ```bash
    git clone https://github.com/nNEWBE/restricted-file-sharing-system-api.git
    cd restricted-file-sharing-system-api
    ```

2.  **Configure Application Properties**
    Open `src/main/resources/application.yaml` and configure your email credentials and JWT secret.

    ```yaml
    mail:
      host: smtp.gmail.com
      port: 587
      username: your-email@gmail.com # Your Gmail address
      password: your-app-password    # Your Gmail App Password
    
    app:
      jwt:
        secret: your-very-long-secret-key-must-be-at-least-256-bits
    ```
    > **Note**: For Gmail, you must use an **App Password** if 2-Step Verification is enabled.

3.  **Run the Application**
    ```bash
    ./gradlew bootRun
    ```
    The application will start on `http://localhost:8080`.

## API Documentation

A comprehensive **Postman Collection** is included in the project root:
📄 `Restricted_File_Sharing_System.postman_collection.json`

Import this file into Postman to test all endpoints.

### Key Endpoints

| Module | Method | Endpoint | Description |
| :--- | :--- | :--- | :--- |
| **Auth** | `POST` | `/api/auth/register` | Register a new account |
| | `POST` | `/api/auth/login` | Authenticate and get Bearer Token |
| | `GET` | `/api/auth/verify?token=...` | Verify email address |
| **Files** | `POST` | `/api/files/upload` | Upload a file (Multipart) |
| | `GET` | `/api/files` | List files you own or have access to |
| | `POST` | `/api/files/{id}/share` | Share a file with specific emails |
| | `GET` | `/api/files/download/{token}` | Download a file (Requires Auth) |

## Database Access
The application uses an H2 file-based database.

*   **Console URL**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
*   **JDBC URL**: `jdbc:h2:file:./data/filedb`
*   **User**: `sa`
*   **Password**: `password`

## Project Structure
```
src/main/java/com/example/restricted_file_sharing_system/
├── config/          # Security and App configuration
├── controller/      # REST Controllers
├── dto/             # Data Transfer Objects
├── entity/          # JPA Entities (User, SharedFile, FileShare)
├── exception/       # Custom exceptions and Global Handler
├── repository/      # Data Access Layer
├── security/        # JWT Filter, Provider, UserDetails
└── service/         # Business Logic (Auth, File, Email)
```
