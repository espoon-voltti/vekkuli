import com.github.gradle.node.npm.task.NpxTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
    id("org.flywaydb.flyway") version "10.12.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    id("com.github.node-gradle.node") version "7.0.2"

    idea
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
}

val e2eTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

configurations["e2eTestRuntimeOnly"].extendsFrom(configurations.testRuntimeOnly.get())

idea {
    module {
        testSources = testSources + sourceSets["e2eTest"].kotlin.sourceDirectories
    }
}

dependencies {
    api(kotlin("stdlib"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // cve fixes
    api("org.yaml:snakeyaml:2.2")

    api("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.ws:spring-ws-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:3.3.0")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.flywaydb:flyway-core")
    implementation("org.postgresql:postgresql:42.7.0")
    api(platform("org.jdbi:jdbi3-bom:3.45.0"))
    implementation("org.jdbi:jdbi3-core")
    implementation("org.jdbi:jdbi3-jackson2")
    implementation("org.jdbi:jdbi3-kotlin")
    implementation("org.jdbi:jdbi3-postgres")

    api(platform("com.fasterxml.jackson:jackson-bom:2.17.0"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("com.auth0:java-jwt:4.4.0")

    implementation("net.logstash.logback:logstash-logback-encoder:7.4")
    implementation("ch.qos.logback:logback-access")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    api(platform("org.junit:junit-bom:5.10.0"))
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.11.0")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.microsoft.playwright:playwright:1.42.0")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
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
            }
            .groupBy({ (_, count) -> count }) { (name, _) -> name }
            .forEach { (count, names) ->
                println(
                    "Resolved $count dependency files for configurations: ${names.joinToString(", ")}"
                )
            }
    }
}

tasks.register("compileSass", NpxTask::class) {
    dependsOn("npmInstall") // Ensure npm install is run before this task
    command = "sass"
    args = listOf("--load-path=node_modules", "src/main/resources/static/sass:src/main/resources/static/css")
}

tasks {
    bootRun {
        dependsOn("compileSass")
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
}

flyway {
    url = "jdbc:postgresql://localhost:5432/vekkuli"
    user = "vekkuli"
    password = "postgres"
    cleanDisabled = false
}
