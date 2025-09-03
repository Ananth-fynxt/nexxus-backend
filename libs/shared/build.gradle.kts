plugins {
    java
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    implementation(libs.spring.boot.starter.jdbc)
    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.h2)
}