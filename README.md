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

## 🚀 Getting Started

### Prerequisites
- Java 21+
- Docker & Docker Compose
- Maven

### Running with Docker Compose

```sh
# From the project root
# Build and start all services
./mvnw clean package -DskipTests
# Then run:
docker-compose up --build
```

- User Service: http://localhost:8081/swagger-ui.html
- Driver Service: http://localhost:8082/swagger-ui.html
- MySQL: localhost:3306 (user: root, pass: rootpass)

### Running Locally (without Docker)
- Start MySQL locally with a database named `cabdb`.
- Run Eureka Server, then User and Driver services via Maven:
  ```sh
  cd eureka-server && ./mvnw spring-boot:run
  cd ../user-service && ./mvnw spring-boot:run
  cd ../driver-service && ./mvnw spring-boot:run
  ```

---

## 📄 API Documentation
Swagger UI:

- User Service: http://localhost:8081/swagger-ui.html
- Driver Service: http://localhost:8082/swagger-ui.html

---

## 🔗 Postman Collections
✅ Postman collections for:

- user-service APIs (register, login, manage)
- driver-service APIs (register, upload docs, admin features)

---

## 🤝 Contributing
1. Fork the repo and create your branch from `main`.
2. Ensure code follows existing style and conventions.
3. Add tests for new features.
4. Submit a pull request with a clear description.

---

## 👤 Author
Susheel Kumar  
📧 ksusheel2575@gmail.com

---

> _For issues, suggestions, or feature requests, please open an issue or contact the author._