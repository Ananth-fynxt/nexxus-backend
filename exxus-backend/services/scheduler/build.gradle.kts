plugins {
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management")
    java
}

dependencies {
    implementation(project(":libs:integration:cron"))
    implementation("org.jobrunr:jobrunr-spring-boot-3-starter:6.3.1")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

