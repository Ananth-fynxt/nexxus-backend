plugins {
    java
    alias(libs.plugins.spring.dependency.management)
}

// Apply common configuration
apply(from = "${rootProject.projectDir}/gradle/common-build.gradle.kts")