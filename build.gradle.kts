plugins {
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
    alias(libs.plugins.spotless) apply false
}

// Apply common configuration to all projects
allprojects {
    apply(from = "${rootProject.projectDir}/gradle/common-build.gradle.kts")
    
    // Apply common plugins to all projects
    plugins.withId("java") {
        apply(plugin = "io.spring.dependency-management")
        
        // Configure Java toolchain for all Java projects
        configure<JavaPluginExtension> {
            toolchain {
                languageVersion = JavaLanguageVersion.of(21)
            }
        }
    }
}
