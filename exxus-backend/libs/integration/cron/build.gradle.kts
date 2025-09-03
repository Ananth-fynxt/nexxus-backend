plugins {
	`java-library`
}

dependencies {
	api("org.jobrunr:jobrunr-spring-boot-3-starter:6.3.1")
	implementation("org.springframework.boot:spring-boot-starter")
	testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}
