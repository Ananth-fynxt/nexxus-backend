plugins {
    id("build.service")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation(platform(libs.spring.boot.bom))

    implementation(project(":libs:integration:cron"))
    implementation(libs.jobrunr.starter)

    implementation(libs.spring.boot.starter.webflux)
    // JDBC strictly for scheduler service for JobRunr storage provider
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    runtimeOnly(libs.postgresql)
}

