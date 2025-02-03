import com.github.gradle.node.npm.task.NpxTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.spring") version "1.9.20"
    id("org.flywaydb.flyway") version "10.20.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    id("com.github.node-gradle.node") version "7.1.0"
    kotlin("plugin.serialization") version "2.0.20"

    idea
}

buildscript {
    dependencies {
        classpath("org.postgresql:postgresql:42.7.1")
        classpath("org.flywaydb:flyway-database-postgresql:11.2.0")
    }
}

node {
    version.set("16.13.0")
    npmVersion.set("8.1.0")
    download.set(true)
    workDir = file("${project.projectDir}/.gradle/nodejs") // Set the node work directory
    npmWorkDir = file("${project.projectDir}/.gradle/npm") // Set the npm work directory
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

sourceSets {
    register("e2eTest") {
        compileClasspath += main.get().output + test.get().output
        runtimeClasspath += main.get().output + test.get().output
    }
    register("integrationTest") {
        compileClasspath += main.get().output + test.get().output
        runtimeClasspath += main.get().output + test.get().output
    }
}

val e2eTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}
val integrationTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

configurations["e2eTestRuntimeOnly"].extendsFrom(configurations.testRuntimeOnly.get())

idea {
    module {
        testSources = testSources + sourceSets["e2eTest"].kotlin.sourceDirectories
        testSources = testSources + sourceSets["integrationTest"].kotlin.sourceDirectories
    }
}

dependencies {
    api(kotlin("stdlib"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // cve fixes
    api("org.yaml:snakeyaml:2.3")

    api("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.ws:spring-ws-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:3.4.0")
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("org.flywaydb:flyway-core:10.20.0")
    implementation("org.flywaydb:flyway-database-postgresql:11.2.0")
    implementation("org.postgresql:postgresql:42.7.1")
    api(platform("org.jdbi:jdbi3-bom:3.45.0"))
    implementation("org.jdbi:jdbi3-core")
    implementation("org.jdbi:jdbi3-jackson2")
    implementation("org.jdbi:jdbi3-kotlin")
    implementation("org.jdbi:jdbi3-postgres")

    api(platform("com.fasterxml.jackson:jackson-bom:2.18.0"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("com.auth0:java-jwt:4.4.0")

    implementation("net.logstash.logback:logstash-logback-encoder:7.4")
    implementation("ch.qos.logback:logback-access")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    api(platform("org.junit:junit-bom:5.11.2"))
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.11.0")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.microsoft.playwright:playwright:1.47.0")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("io.ktor:ktor-client-core:3.0.3")
    implementation("io.ktor:ktor-client-cio:3.0.3") // CIO engine
    implementation("io.ktor:ktor-client-content-negotiation:3.0.3") // Content negotiation
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.3") // kotlinx serialization
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.10.1")
    implementation("org.reactivestreams:reactive-streams:1.0.4")
    implementation("software.amazon.awssdk:ses:2.30.8")
    implementation("software.amazon.awssdk:core:2.28.26")
    implementation("software.amazon.awssdk:regions:2.20.0")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.commonmark:commonmark:0.24.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    implementation("org.unbescape:unbescape:1.1.6.RELEASE")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    outputs.upToDateWhen { false }
}

tasks.getByName<Jar>("jar") {
    archiveClassifier.set("")
}

tasks.getByName<BootJar>("bootJar") {
    archiveClassifier.set("boot")
}

tasks.register("resolveDependencies") {
    description = "Resolves all dependencies"
    doLast {
        configurations
            .matching { it.isCanBeResolved }
            .map {
                val files = it.resolve()
                it.name to files.size
            }.groupBy({ (_, count) -> count }) { (name, _) -> name }
            .forEach { (count, names) ->
                println(
                    "Resolved $count dependency files for configurations: ${names.joinToString(", ")}"
                )
            }
    }
}

tasks.register("compileSass", NpxTask::class) {
    dependsOn("npmInstall") // Dependencies from package.json are installed
    command = "sass"
    args =
        listOf(
            "--load-path=node_modules",
            "src/main/resources/public/virkailija/static/sass:src/main/resources/public/virkailija/static/css"
        )
}

tasks.register("bundleJs", NpxTask::class) {
    dependsOn("npmInstall")
    command = "webpack"
    args = listOf("--config", "webpack.config.js") // Optional: specify config path if needed
}

tasks {
    bootRun {
        dependsOn("compileSass", "bundleJs")
        systemProperty("spring.profiles.active", "local")
    }

    register("e2eTestDeps", JavaExec::class) {
        group = "build"
        classpath = sourceSets["e2eTest"].runtimeClasspath
        mainClass = "com.microsoft.playwright.CLI"
        args("install-deps")
    }

    register("e2eTest", Test::class) {
        useJUnitPlatform()
        group = "verification"
        testClassesDirs = sourceSets["e2eTest"].output.classesDirs
        classpath = sourceSets["e2eTest"].runtimeClasspath
        shouldRunAfter("test")
        outputs.upToDateWhen { false }
        testLogging {
            showStandardStreams = true
            events("passed", "skipped", "failed")
        }
    }
    register("integrationTest", Test::class) {
        useJUnitPlatform()
        group = "verification"
        testClassesDirs = sourceSets["integrationTest"].output.classesDirs
        classpath = sourceSets["integrationTest"].runtimeClasspath
        shouldRunAfter("test")
        outputs.upToDateWhen { false }
        testLogging {
            showStandardStreams = true
            events("passed", "skipped", "failed")
        }
    }
}

flyway {
    url = "jdbc:postgresql://localhost:5432/vekkuli"
    user = "vekkuli"
    password = "postgres"
    cleanDisabled = false
}
