plugins {
    id("build.library")
}

dependencies {
    implementation(platform(libs.spring.boot.bom))

    // Expose JobRunr starter transitively to consumers
    api(libs.jobrunr.spring.boot.starter)

    // Development Tools
    implementation(libs.lombok)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

