plugins {
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management")
    java
}

dependencies {
    implementation(project(":libs:integration:cron"))
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    runtimeOnly("io.r2dbc:r2dbc-postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

