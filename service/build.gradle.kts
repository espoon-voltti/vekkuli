import com.github.gradle.node.npm.task.NpxTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.spring") version "2.1.10"
    id("org.flywaydb.flyway") version "11.3.1"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.2"
    id("com.github.node-gradle.node") version "7.1.0"
    kotlin("plugin.serialization") version "2.0.20"
    id("org.owasp.dependencycheck") version "12.1.0"

    idea
}

ktlint {
    version.set("1.5.0")
}

buildscript {
    dependencies {
        classpath("org.postgresql:postgresql:42.7.1")
        classpath("org.flywaydb:flyway-database-postgresql:11.3.0")
    }
}

node {
    version.set("20.11.0") // Latest LTS version
    npmVersion.set("10.1.0")
    download.set(true)
    workDir = file("${project.projectDir}/.gradle/nodejs") // Set the node work directory
    npmWorkDir = file("${project.projectDir}/.gradle/npm") // Set the npm work directory
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
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
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // cve fixes
    implementation("org.yaml:snakeyaml:2.3")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:3.4.0")
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("org.flywaydb:flyway-core:10.20.0")
    implementation("org.flywaydb:flyway-database-postgresql:11.3.0")
    implementation("org.postgresql:postgresql:42.7.1")
    implementation(platform("org.jdbi:jdbi3-bom:3.47.0"))
    implementation("org.jdbi:jdbi3-core")
    implementation("org.jdbi:jdbi3-jackson2")
    implementation("org.jdbi:jdbi3-kotlin")
    implementation("org.jdbi:jdbi3-postgres")

    implementation(platform("com.fasterxml.jackson:jackson-bom:2.18.2"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("com.auth0:java-jwt:4.5.0")

    implementation("net.logstash.logback:logstash-logback-encoder:7.4")
    implementation("ch.qos.logback.access:logback-access-tomcat:2.0.5")

    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    implementation(platform("org.junit:junit-bom:5.11.4"))
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.11.0")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.microsoft.playwright:playwright:1.47.0")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("io.ktor:ktor-client-core:3.1.0")
    implementation("io.ktor:ktor-client-cio:3.1.0") // CIO engine
    implementation("io.ktor:ktor-client-content-negotiation:3.1.0") // Content negotiation
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.3") // kotlinx serialization
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.10.1")
    implementation("org.reactivestreams:reactive-streams:1.0.4")

    implementation(platform("software.amazon.awssdk:bom:2.30.18"))
    implementation("software.amazon.awssdk:ses")
    implementation("software.amazon.awssdk:regions")

    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.commonmark:commonmark:0.24.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    implementation("org.unbescape:unbescape:1.1.6.RELEASE")

    implementation("ch.qos.logback:logback-classic:1.5.16")
    implementation("ch.qos.logback:logback-core:1.5.16")
    implementation("commons-codec:commons-codec:1.18.0")
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.JVM_21)
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

    dependencyCheck {
        failBuildOnCVSS = 0.0f
        analyzers.apply {
            assemblyEnabled = false
            nodeAuditEnabled = false
            nodeEnabled = false
            nuspecEnabled = false
        }
        nvd.apply { apiKey = System.getenv("NVD_API_KEY") }
        suppressionFile = "$projectDir/owasp-suppressions.xml"
    }
}

flyway {
    url = "jdbc:postgresql://localhost:5432/vekkuli"
    user = "vekkuli"
    password = "postgres"
    cleanDisabled = false
}
