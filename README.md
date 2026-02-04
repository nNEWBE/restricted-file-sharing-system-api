# 🔐 Restricted File Sharing System (RFSS)

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-JSON%20Web%20Token-black?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![H2](https://img.shields.io/badge/Database-H2-005C84?style=for-the-badge&logo=hibernate&logoColor=white)

A high-security, enterprise-ready backend system for controlled file exchange. RFSS ensures that your data remains private by default, allowing file access only to authenticated, verified, and explicitly authorized users.

---

## 🚀 Vision
In modern digital environments, public link sharing is often insecure. RFSS bridges the gap between ease of use and maximum security by implementing a **private-first** architecture. Every file is protected by a multi-layer verification system.

## ✨ Core Features

### 🔐 Secure Identity Management
*   **Zero-Trust Registration**: New accounts are dormant until verified via a secure SMTP-driven link.
*   **Ephemeral Verification**: Verification tokens are strictly time-bound (10-minute TTL).
*   **JWT Authorization**: All secured endpoints require a valid Bearer Token derived from identity verification.

### 📁 Advanced File Security
*   **Encapsulated Storage**: Files are mapped to internal UUIDs, preventing direct file system discovery.
*   **Non-Ephemeral Persistence**: Designed for project-based collaboration where files need to persist for the lifetime of a project.
*   **Ownership Integrity**: Track the origin of every upload with immutable owner relationships.

### 🤝 Restricted Peer-to-Peer Sharing
*   **Whitelist Logic**: Share files only with existing, verified members of the system.
*   **Granular Authorization**: Even with a valid download token, the system validates the requester's identity against the authorized recipient whitelist.
*   **Automated Notifications**: Real-time email alerts for shared resources.

---

## 🛠️ Tech Stack
| Tier | Technology | Rationale |
| :--- | :--- | :--- |
| **Framework** | Spring Boot 3.4 | Robust dependency injection and rapid API development. |
| **Security** | Spring Security 6 | Industry-standard protection and JWT integration. |
| **Database** | H2 Database | High-performance, file-based persistence for easy portability. |
| **Messaging** | Java Mail Sender | Real-time SMTP integration for secure verification. |
| **ORM** | Hibernate / JPA | Type-safe data access and simplified schema migrations. |

---

## 📖 API Reference

### Authentication Endpoints
| Method | Endpoint | Description | Payload |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Create a new user account | `email`, `password` |
| `POST` | `/api/auth/login` | Obtain a JWT Bearer Token | `email`, `password` |
| `GET` | `/api/auth/verify` | Activate account via query token | `token` |

### File Management Endpoints (Requires JWT)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/files/upload` | Multipart upload to private storage |
| `GET` | `/api/files` | Retrieve accessible resources (Owned & Shared) |
| `POST` | `/api/files/{id}/share` | Grant access to a whitelist of emails |
| `GET` | `/api/files/download/{token}` | Authorized resource retrieval |

---

## ⚙️ Configuration & Setup

### 1. Prerequisites
*   **JDK 21 LTS**
*   **Gradle 8.x**
*   **SMTP Provider** (e.g., Gmail App Password)

### 2. Environment Variables
RFSS uses `application.yaml` with variable interpolation. You can set these in your environment or update the file directly:

```yaml
spring:
  mail:
    username: ${MAIL_USERNAME} # Your Gmail
    password: ${MAIL_PASSWORD} # Your App Password
  
app:
  jwt:
    secret: ${JWT_SECRET} # Minimum 256-bit string
```

### 3. Installation
```bash
# Clone the repository
git clone https://github.com/nNEWBE/restricted-file-sharing-system-api.git

# Build the project
./gradlew build

# Launch the server
./gradlew bootRun
```

---

## 🧪 Testing with Postman
A pre-configured test suite is included in the project:
`Restricted_File_Sharing_System.postman_collection.json`

1.  Import the collection into Postman.
2.  Set the `base_url` variable.
3.  Register a user -> Check console/email for token -> Verify account.
4.  Login to populate the `jwt_token`.

---

## 🏗️ Project Architecture
```text
src/main/java/com/example/restricted_file_sharing_system/
├── config/       # Security Policy & Bean Definitions
├── controller/   # REST API Entry Points
├── dto/          # Contract Data Structures
├── entity/       # Database Domain Models
├── exception/    # Error Handling Middleware
├── repository/   # Persistence Interfaces
├── security/     # JWT & Identity Logic
└── service/      # Business & Domain Logic
```

---

## 📝 License
Distributed under the MIT License. See `LICENSE` for more information.

Developed with ❤️ for secure collaboration.
