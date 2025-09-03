/*
 * Multi-module Spring Boot microservices workspace
 */

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "nexxus-backend"

include(
    "eureka-server",
    "api-gateway",
    "config-server",
    "user-service",
    "order-service",
)
