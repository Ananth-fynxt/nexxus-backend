plugins {
	alias(libs.plugins.spring.boot) apply false
	alias(libs.plugins.spring.dependency.management) apply false
	alias(libs.plugins.spotless) apply false
}

allprojects {
	repositories {
		mavenCentral()
	}
}

subprojects {
	plugins.withId("java") {
		configure<JavaPluginExtension> {
			toolchain {
				languageVersion = JavaLanguageVersion.of(21)
			}
		}
	}
}
