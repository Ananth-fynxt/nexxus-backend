plugins {
    id("build.service")
}

dependencies {
    implementation(platform(libs.spring.boot.bom))

    implementation(libs.bundles.spring.boot.web)
    // Use JDBC only for JobRunr storage
    implementation(libs.spring.boot.starter.jdbc)
    implementation(libs.postgresql)

    implementation(project(":libs:integration:cron"))

    implementation(libs.lombok)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

springBoot {
    mainClass.set("nexxus.scheduler.SchedulerApplication")
}

