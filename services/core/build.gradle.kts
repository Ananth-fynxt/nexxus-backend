plugins {
	alias(libs.plugins.spring.boot)
	alias(libs.plugins.spring.dependency.management)
	java
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(libs.spring.boot.starter.web)
	implementation(libs.spring.boot.starter.jdbc)
	runtimeOnly(libs.postgresql)
	runtimeOnly(libs.h2)

	testImplementation(libs.spring.boot.starter.test)
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

springBoot {
	mainClass.set("nexxus.NexxusBackendApplication")
}

tasks.named<Test>("test") {
	useJUnitPlatform()
}
