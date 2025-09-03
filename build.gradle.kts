plugins {
    id("org.springframework.boot") version "3.5.3" apply false
    id("io.spring.dependency-management") version "1.1.6" apply false
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    plugins.apply("java")

    extensions.configure<org.gradle.api.plugins.JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    dependencies {
        add("implementation", platform("org.springframework.boot:spring-boot-dependencies:3.5.3"))
        add("implementation", platform("org.springframework.cloud:spring-cloud-dependencies:2024.0.2"))
        add("testImplementation", "org.springframework.boot:spring-boot-starter-test")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    configurations.all {
        resolutionStrategy.eachDependency {}
    }
}

