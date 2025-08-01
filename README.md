🚖 **Cab Booking Application – Microservices Architecture**

![Java](https://img.shields.io/badge/Java-21-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Docker](https://img.shields.io/badge/Docker-Ready-blue)
![Status](https://img.shields.io/badge/Status-Active-green)

---

A production-ready, scalable, and secure cab booking system built with Spring Boot using microservices architecture. This platform supports separate services for user management, driver operations, and will soon support booking, payments, and real-time tracking.

---

## 📑 Table of Contents
- [Objectives](#objectives)
- [Microservices Breakdown](#microservices-breakdown)
- [Security Highlights](#security-highlights)
- [Current Features](#current-features)
- [Infra Setup](#infra-setup)
- [Tech Stack](#tech-stack)
- [System Architecture](#system-architecture)
- [Planned Work](#planned-work)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Postman Collections](#postman-collections)
- [Contributing](#contributing)
- [Author](#author)

---

## 📌 Objectives
- Modular design for independent service development.
- Role-based access control with secure APIs.
- Admin management of users and drivers.
- Designed to support future extensions like ride booking, payments, and tracking.

---

## 🧩 Microservices Breakdown
1. **Eureka Server**  
   Acts as a Service Registry for all microservices.  
   Location: `/eureka-server`  
   ✅ Configured and running.

2. **User-Service**  
   Handles everything related to users:
   - Registration & Login
   - Email verification
   - Password management
   - Role & authority management
   - JWT-based security
   - REST endpoints secured using Spring Security

3. **Driver-Service**  
   Handles driver operations:
   - Registration & profile management
   - Uploading and verifying documents
   - Availability & location update
   - Includes Admin module for:
     - Approving drivers
     - Changing driver roles/status
     - Viewing driver documents

4. **API Gateway (Planned)**  
   To serve as a single entry point for all client requests.
   - Will handle global auth, routing, and rate limiting.

5. **Booking-Service (Planned)**  
   Will handle:
   - Ride requests from users
   - Driver assignment logic
   - Ride tracking and status updates

6. **Notification-Service (Planned)**  
   For sending email/SMS notifications to users and drivers.

7. **Payment-Service (Planned)**  
   To manage transactions, payment methods, and fare calculations.

8. **Location-Tracking Service (Planned)**  
   Real-time location updates using WebSocket
   - Geofencing and nearby driver search
   - Integration with mapping services
   - Location history and path optimization

9. **Real-time Communication Service (Planned)**  
   Built with Apache Kafka for event streaming
   - WebSocket for real-time updates
   - Driver location broadcasts
   - Ride status updates
   - Chat between driver and passenger

---

## 🔐 Security Highlights
- JWT based stateless authentication
- Role-based access using Spring Security:
  - Users → access personal data
  - Admins → manage all users/drivers
- Pre-authorized endpoints with fine-grained permissions like `ADMIN_READ`, `ADMIN_UPDATE`

---

## ✅ Current Features
### 🧑 User-Service
- User registration with roles
- Email verification via token
- Login + JWT generation
- Forgot/reset/change password
- Admin-level operations:
  - View/search/filter users
  - Enable/disable accounts
  - Lock/unlock accounts
  - Update user roles

### 🚗 Driver-Service
- Driver registration
- Profile update, location, and availability
- Upload documents (like license, ID)
- Admin operations:
  - View/search drivers
  - Change driver roles/status
  - Verify documents
  - Delete drivers
  - Fetch statistics

---

## 🔧 Infra Setup
- ✅ Eureka Server running
- ✅ Swagger/OpenAPI for all services
- ✅ Postman Collections for:
  - User-service APIs
  - Driver-service APIs
- ✅ Spring Security integrated with JWT
- ✅ Docker Compose for local development

---

## 🛠️ Tech Stack
| Layer            | Technology                                      |
|------------------|-------------------------------------------------|
| Language         | Java 21                                         |
| Framework        | Spring Boot 3, Spring Security                 |
| Service Reg.     | Spring Cloud Eureka                            |
| Message Queue    | Apache Kafka                                   |
| Real-time Comm   | WebSocket (Spring WebSocket)                   |
| Geolocation      | PostGIS / MongoDB Geospatial                   |
| API Docs         | Springdoc OpenAPI + Swagger UI                 |
| Database         | MySQL                                           |
| Cache            | Redis                                           |
| Auth             | JWT + Spring Security                          |
| API Testing      | Postman                                        |
| Containerization | Docker                                          |
| Orchestration    | Kubernetes (planned)                          |
| CI/CD            | Jenkins (planned)                             |
| Deployment       | AWS/GCP (planned)                             |
| Monitoring       | ELK Stack, Prometheus + Grafana               |

---

## 📂 System Architecture
[Planned Components]
1. **Real-time Location Tracking**
   - WebSocket connections for live location updates
   - Kafka topics for location event streaming
   - Geospatial indexing for efficient driver search
   - Location update throttling and optimization

2. **Message Queuing System**
   - Kafka topics for:
     * Driver location updates
     * Ride status changes
     * Payment events
     * Notifications
   - Consumer groups for scalable processing
   - Event persistence for analytics

3. **Caching Layer**
   - Redis for:
     * Active driver locations
     * Ride status
     * User sessions
     * Rate limiting

4. **High Availability**
   - Service replication
   - Database clustering
   - Kafka clustering
   - Redis sentinel/cluster

5. **Monitoring & Analytics**
   - ELK Stack for log aggregation
   - Prometheus + Grafana for metrics
   - Custom dashboards for:
     * Active rides
     * Driver distribution
     * System health
     * Performance metrics

---

## 🔭 Planned Work
- Spring Cloud Gateway setup
- Booking service
- Notification service
- Payment handling
- Dockerize each service
- Docker Compose setup
- Kubernetes deployment
- Jenkins pipeline setup
- Monitoring: ELK/Prometheus + Grafana
- Cloud deployment on AWS/GCP
- Set up Kafka for event streaming
- Implement WebSocket for real-time updates
- Add PostGIS/MongoDB for location tracking
- Implement Redis caching
- Set up ELK stack for logging

---

## 🚦 Audit Logging & Traceability

All critical admin and user actions are audit-logged for compliance and traceability:
- **User-Service**: All admin/user actions (PUT, POST, DELETE to `/api/users/**`) are logged to the `audit_logs` table. Paginated endpoint for review:
  - `GET /api/users/audit-logs?page=0&size=20` (ADMIN_READ required)
- **Driver-Service**: All admin actions (via HandlerInterceptor) are logged to the `audit_logs` table. Paginated endpoint for review:
  - `GET /api/v1/admin/audit-logs?page=0&size=20` (ADMIN role required)
- **Logging**: All key service methods use SLF4J logging for observability and debugging.

---

## 📡 Real-Time Location Tracking & Notifications

- **Location-Tracking-Service**:
  - Consumes driver location updates from Kafka.
  - Persists locations in MySQL.
  - Provides REST APIs for querying locations:
    - `GET /api/driver/{driverId}/locations?limit=10` – Recent locations for a driver
    - `GET /api/drivers/locations` – Latest location for all drivers
    - `GET /api/drivers/locations/filter?city=...&status=...` – Filtered by city/status
  - Broadcasts live locations via WebSocket.
  - Sends notification events to Kafka when driver status is AVAILABLE.

- **Notification-Service**:
  - Consumes notification events from Kafka.
  - Logs notification processing (stub for email/SMS/push).

---

## 🔗 Key REST Endpoints

### User-Service
- `POST /api/users/register` – Register user
- `POST /api/users/login` – Login
- `GET /api/users/profile` – Get user profile
- `PUT /api/users/profile` – Update profile
- `PUT /api/users/change-password` – Change password
- `GET /api/users` – List all users (ADMIN_READ)
- `GET /api/users/search?keyword=...` – Search users (ADMIN_READ)
- `PUT /api/users/{email}/roles` – Update user roles (ADMIN_UPDATE)
- `PUT /api/users/{email}/toggle-status` – Enable/disable user (ADMIN_UPDATE)
- `PUT /api/users/{email}/toggle-lock` – Lock/unlock user (ADMIN_UPDATE)
- `DELETE /api/users/{email}` – Delete user (ADMIN_DELETE)
- `GET /api/users/audit-logs` – Paginated audit logs (ADMIN_READ)

### Driver-Service (Admin)
- `GET /api/v1/admin/drivers` – List drivers (paginated)
- `GET /api/v1/admin/drivers/search?query=...` – Search drivers
- `GET /api/v1/admin/drivers/{id}` – Get driver by ID
- `PATCH /api/v1/admin/drivers/{id}/status` – Update driver status
- `PATCH /api/v1/admin/drivers/{id}/role` – Update driver role
- `GET /api/v1/admin/drivers/statistics` – Driver statistics
- `GET /api/v1/admin/drivers/{driverId}/documents` – Get driver documents
- `POST /api/v1/admin/drivers/documents/{documentId}/verify` – Verify document
- `GET /api/v1/admin/drivers/available` – List available drivers
- `DELETE /api/v1/admin/drivers/{id}` – Delete driver
- `GET /api/v1/admin/audit-logs` – Paginated audit logs

### Location-Tracking-Service
- `GET /api/driver/{driverId}/locations?limit=10` – Recent locations for a driver
- `GET /api/drivers/locations` – Latest location for all drivers
- `GET /api/drivers/locations/filter?city=...&status=...` – Filtered by city/status

---

## ⚡ Event-Driven & Real-Time Features
- **Kafka**: Used for driver location updates and notification events.
- **WebSocket**: Real-time location broadcasting to clients.
- **Notification-Service**: Consumes events and logs (stub for future email/SMS/push integration).

---

## 📝 Usage Notes
- All endpoints are protected by JWT and role-based access control.
- Audit logs are available for admin review in both user-service and driver-service.
- Real-time and event-driven features are enabled by default in Docker Compose.

---

## 🛠️ How to Run (Quick Start)

```powershell
# From the project root
./mvnw clean package -DskipTests
# Then run:
docker-compose up --build
```

- User Service: http://localhost:8081/swagger-ui.html
- Driver Service: http://localhost:8082/swagger-ui.html
- Location Tracking Service: [port as configured]
- Notification Service: [port as configured]
- MySQL: localhost:3306 (user: root, pass: rootpass)

---

## 📈 Extensibility
- Add more granular audit log details (e.g., request bodies, action types) as needed.
- Implement real notification channels (email, SMS, push) in notification-service.
- Add advanced analytics, geospatial queries, or further admin/user endpoints as needed.

---

## 👤 Author
Susheel Kumar  
📧 ksusheel2575@gmail.com

---

> _For issues, suggestions, or feature requests, please open an issue or contact the author._