plugins {
	kotlin("jvm") version "1.9.25"
	id("java")
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.serialization") version "1.8.0" // Use the version you are using
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

tasks.jar {
    archiveBaseName.set("your-app")  // Set the JAR file name (without the version)
    archiveVersion.set("1.0.0")      // Set the JAR version
    manifest {
        attributes["Main-Class"] = "com.example.demo.DemoApplicationKt" // Set the entry point for your app
    }
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.springframework.boot:spring-boot-starter:2.7.10") // Specify version
    implementation("io.ktor:ktor-client-core:2.3.0")
    implementation("io.ktor:ktor-client-cio:2.3.0")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.0")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("com.sendgrid:sendgrid-java:4.7.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
	// For Kotlin coroutines
	implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core")
	implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation ("org.springframework.boot:spring-boot-starter-webflux")
	implementation("io.ktor:ktor-client-logging:2.3.4")
}


kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
