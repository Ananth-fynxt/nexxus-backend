plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

// Apply common configuration
apply(from = "${rootProject.projectDir}/gradle/common-build.gradle.kts")

dependencies {
    implementation(libs.bundles.database)
    compileOnly(libs.bundles.development)
    annotationProcessor(libs.bundles.development)
}

// Disable bootJar for migration service
tasks.bootJar {
    enabled = false
}
