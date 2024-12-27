plugins {
    kotlin("jvm") version "2.0.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/lets-plot/maven")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.lets-plot:lets-plot-kotlin:3.2.0")
    implementation("org.jetbrains.lets-plot:lets-plot-common:3.2.0")
    implementation("org.jetbrains.lets-plot:lets-plot-image-export:3.2.0")
    implementation("org.jetbrains.lets-plot:lets-plot-batik:3.2.0") // Add this line
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}
