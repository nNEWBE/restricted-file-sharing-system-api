# Restricted File Sharing System

A secure, backend-only file sharing system where users must be authenticated and verified to upload files. Files are not public; they can only be shared with specific registered users.

## Features

1.  **User Authentication**:
    *   Registration with email and password.
    *   Email verification (link valid for 10 minutes).
    *   JWT-based login.
    *   Access restricted to verified users only.

2.  **File Management**:
    *   Authenticated users can upload files.
    *   Files are stored permanently (not deleted).
    *   Each file has a unique, permanent download link.

3.  **Restricted Sharing**:
    *   Files are private by default (only visible to owner).
    *   Owners can share files with other registered users via email.
    *   Only the owner and explicitly shared users can download the file.
    *   Unauthorized access attempts return errors.

4.  **Notifications**:
    *   Email sent upon registration for verification.
    *   Email notification sent to users when a file is shared with them.

## Technology Stack

*   **Java 21**
*   **Spring Boot 3.4.2**
*   **Spring Security** (JWT Authentication)
*   **H2 Database** (In-memory, persists to file)
*   **JavaMailSender** (for emails)
*   **Gradle**

## Getting Started

### Prerequisites

*   Java 21 SDK installed.

### Configuration

The application uses **Mailtrap** for email testing by default. You can configure your credentials in `src/main/resources/application.yaml`.

```yaml
spring:
  mail:
    username: <your-mailtrap-username>
    password: <your-mailtrap-password>
```

### Running the Application

1.  Open the project in your IDE (IntelliJ IDEA recommended).
2.  Run the `RestrictedFileSharingSystemApplication` class.
3.  The server will start at `http://localhost:8080`.

### API Endpoints

#### Authentication

*   `POST /api/auth/register` - Register a new user
    ```json
    {
      "email": "user@example.com",
      "password": "password123"
    }
    ```
*   `GET /api/auth/verify?token={token}` - Verify email (link sent via email)
*   `POST /api/auth/login` - Login to get JWT token
    ```json
    {
      "email": "user@example.com",
      "password": "password123"
    }
    ```

#### File Operations (Requires Bearer Token)

*   `POST /api/files/upload` - Upload a file (multipart/form-data, key=`file`)
*   `POST /api/files/{fileId}/share` - Share a file with users
    ```json
    {
      "emails": ["friend@example.com", "colleague@example.com"]
    }
    ```
*   `GET /api/files` - List all accessible files (owned + shared)
*   `GET /api/files/download/{token}` - Download a file

## Database Console

*   Access H2 Console at: `http://localhost:8080/h2-console`
*   JDBC URL: `jdbc:h2:file:./data/filedb`
*   User: `sa`
*   Password: `password`
