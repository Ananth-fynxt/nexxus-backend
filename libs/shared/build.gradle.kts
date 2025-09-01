plugins {
    id("build.library")
}

dependencies {
    // Spring Boot BOM for version management
    implementation(platform(libs.spring.boot.bom))
    
    // Spring Boot starters (versions managed by BOM)
    api(libs.spring.boot.starter.webflux)
    api(libs.spring.boot.starter.data.r2dbc)
    api(libs.spring.boot.starter.security)
    
    // Database
    api(libs.r2dbc.postgresql)
    
    // JSON Schema Validation
    api(libs.json.schema.validator)
    
    // Jackson for JSON processing (versions managed by BOM)
    api(libs.jackson.databind)
    api(libs.jackson.datatype.jsr310)
    
    // Reactor for reactive programming (version managed by BOM)
    api(libs.reactor.core)
    
    // Development Tools
    implementation(libs.lombok)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    
    // Swagger/OpenAPI Documentation
    api(libs.spring.doc.openapi.starter.webflux.ui)
}
