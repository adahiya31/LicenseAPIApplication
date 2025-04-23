# License Management System

A high-performance *License Management System* built with Spring Boot, designed to streamline the management of users, roles, permissions, and licenses. This system leverages dynamic eligibility rules.

## Key Features

- *User, Role, and Permission Management*: Comprehensive APIs for managing users, roles, and permissions.
- *License Management*:
  - Dynamic eligibility validation based on roles and permissions.
  - APIs to create, update, delete, and retrieve licenses.
- *Security*:
  - JWT Token authentication
- *Dynamic Rules*: Configure eligibility rules for licenses directly in the database.

---

## Technology Stack

- *Java*: Backend implementation.
- *Spring Boot*: Framework for REST API development and dependency management.
- *Spring Security*: JWT Authentication and authorization.
- *Postgres Database*: Lightweight, Docker  containerized database for development and testing.

---

## Setup Guide

### Prerequisites

- Java 21
- Docker
- Maven

### Installation

1. Clone the repository:

2. Build the project:
   ```
   mvn clean install
   ```

3. Run the application:
   ```
   mvn spring-boot:run
   ```

4. Run command
   ```
   docker-compose up --build
   ```

6. Access the API at `http://localhost:8282`.

---

## API Overview

### License APIs
| Method | Endpoint         | Description                    |
|--------|------------------|--------------------------------|
| `POST` | `auth?userId={}`  | Generate Token         |
| `POST` | `license?contentId={}&userId={}`  | Create a new license     |
| `GET`  | `/all/license`  | Retrieve all licenses         |
| `GET`  | `license?contentId={}` | Check Eligibility    |
| `DELETE` | `/licenses/{id}` | Delete a license         |
| `PUT` | `/licenses/{id}` | Update a license         |
| `GET`  | `/license/details` | Check Based on content ID   |


### User APIs
| Method | Endpoint         | Description                    |
|--------|------------------|--------------------------------|
| `POST` | `/api/users`     | Create a new user             |


### Role APIs
| Method | Endpoint         | Description                    |
|--------|------------------|--------------------------------|
| `POST` | `/api/roles`     | Create a new role             |


### Permission APIs
| Method | Endpoint         | Description                    |
|--------|------------------|--------------------------------|
| `POST` | `/api/permissions` | Create a new permission      |


---


## License Eligibility Logic

The `isUserEligibleForLicense` method performs the following checks:
1. *Input Validation*: Ensures `userId` and `contentId` are provided and non-empty.
2. *User Retrieval*: Fetches the user using `userService.getUserById`.
3. *License Validation*: Checks if a license exists for the given content ID, ensuring it belongs to the user and hasn't expired.
4. *Role Validation*: Confirms the user has at least one of the required roles (e.g., `Admin`, `Premium User`).
5. *Permission Validation*: Verifies the user has the necessary permissions (e.g., `LICENSE_ACCESS`).
6. *Dynamic Rules*: Validates against custom eligibility rules stored in the database.

---

## Testing and Validation

Run the test suite using:
```bash
mvn test
```

---
