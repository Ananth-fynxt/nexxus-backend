// Common Spring Boot configuration for modules that need Spring Boot
plugins {
    alias(libs.plugins.spring.boot)
}

// Apply common configuration
apply(from = "${rootProject.projectDir}/gradle/common-build.gradle.kts")

