plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.spotless)
}

// Apply common configuration
apply(from = "${rootProject.projectDir}/gradle/common-build.gradle.kts")

dependencies {
    implementation(project(":libs:shared"))
    implementation(libs.bundles.spring.boot.web)
    compileOnly(libs.bundles.development)
    annotationProcessor(libs.bundles.development)
}

springBoot {
    mainClass.set("nexxus.NexxusBackendApplication")
}