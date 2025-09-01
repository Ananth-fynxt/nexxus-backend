plugins {
    id("build.service")
}

dependencies {
    // Spring Boot BOM for version management
    implementation(platform(libs.spring.boot.bom))
    
    // Spring Boot starters (versions managed by BOM)
    implementation(libs.bundles.spring.boot.web)
    implementation(libs.bundles.database)
    
    // Development Tools
    implementation(libs.lombok)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    
    // Project modules
    implementation(project(":libs:shared"))
}
