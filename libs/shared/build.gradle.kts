plugins {
	java
	alias(libs.plugins.spring.boot)
	alias(libs.plugins.spring.dependency.management)
}

// Disable bootJar for shared library
tasks.bootJar {
	enabled = false
}

dependencies {
	implementation(libs.bundles.database)
	
	compileOnly(libs.bundles.development)
	annotationProcessor(libs.bundles.development)
}