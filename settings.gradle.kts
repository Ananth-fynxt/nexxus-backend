/*
 * Gradle Settings for Nexxus Backend
 * 
 * This file configures the multi-project build structure:
 * - libs/: Shared libraries and utilities
 * - services/: Individual service modules
 */

plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "nexxus-backend"

include("libs:shared")
include("services:migration")
include("services:core")
