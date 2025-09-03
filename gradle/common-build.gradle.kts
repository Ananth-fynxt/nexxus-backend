// Common build configuration for all modules
// This file contains common configurations that will be applied to all projects

// Common repositories
repositories {
    mavenCentral()
}

// Common encoding for all Java compilation tasks
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// Common test configuration
tasks.withType<Test> {
    useJUnitPlatform()
}
