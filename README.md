# Nexxus Backend - Microservices (Spring Boot 3.5, Java 21)

## Modules
- eureka-server
- config-server
- api-gateway
- user-service
- order-service
- config-repo (local Git-backed config)

## Build
```bash
./gradlew clean build -x test
```

## Run with Docker Compose
```bash
docker compose build
# Start in dependency order
docker compose up -d config-server
sleep 5
docker compose up -d eureka
sleep 5
docker compose up -d api-gateway user-service order-service db
```

Access:
- Config Server: http://localhost:8888
- Eureka: http://localhost:8761
- Gateway: http://localhost:8080
- Users: http://localhost:8080/users
- Orders: http://localhost:8080/orders

## Notes
- All ports, URLs, and credentials are configured via environment variables and config server profiles.
