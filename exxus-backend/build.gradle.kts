plugins {
	id("io.spring.dependency-management") version "1.1.6" apply false
	id("org.springframework.boot") version "3.5.4" apply false
	java
}

subprojects {
	apply(plugin = "io.spring.dependency-management")
	repositories { mavenCentral() }
	dependencyManagement {
		imports {
			mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.4")
		}
	}
	plugins.withType<JavaPlugin> {
		configure<JavaPluginExtension> {
			toolchain.languageVersion.set(JavaLanguageVersion.of(21))
		}
	}
}
