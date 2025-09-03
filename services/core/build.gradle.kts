plugins {
	alias(libs.plugins.spring.boot)
	alias(libs.plugins.spring.dependency.management)
	java
}

dependencies {
	implementation(project(":libs:shared"))
	implementation(libs.spring.boot.starter.web)
}

springBoot {
	mainClass.set("nexxus.NexxusBackendApplication")
}