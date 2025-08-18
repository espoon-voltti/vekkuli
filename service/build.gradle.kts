import com.github.gradle.node.npm.task.NpxTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.util.regex.Pattern

plugins {
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "2.2.10"
    kotlin("plugin.spring") version "2.2.10"
    id("org.flywaydb.flyway") version "11.10.5"
    id("org.jlleitschuh.gradle.ktlint") version "13.0.0"
    id("com.github.node-gradle.node") version "7.1.0"
    kotlin("plugin.serialization") version "2.2.10"
    id("org.owasp.dependencycheck") version "12.1.3"

    idea
}

ktlint {
    version.set("1.6.0")
}

buildscript {
    dependencies {
        classpath("org.postgresql:postgresql:42.7.7")
        classpath("org.flywaydb:flyway-database-postgresql:11.10.5")
    }
}

val downloadOnly: Configuration by configurations.creating {
    isTransitive = false
}

node {
    version.set("22.14.0") // Latest LTS version
    npmVersion.set("10.9.2")
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
    implementation("org.yaml:snakeyaml:2.4")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.zaxxer:HikariCP:7.0.1")
    implementation("org.flywaydb:flyway-core:11.10.5")
    implementation("org.flywaydb:flyway-database-postgresql:11.10.5")
    implementation("org.postgresql:postgresql:42.7.7")
    implementation(platform("org.jdbi:jdbi3-bom:3.49.5"))
    implementation("org.jdbi:jdbi3-core")
    implementation("org.jdbi:jdbi3-jackson2")
    implementation("org.jdbi:jdbi3-kotlin")
    implementation("org.jdbi:jdbi3-postgres")

    implementation(platform("com.fasterxml.jackson:jackson-bom:2.19.2"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("com.auth0:java-jwt:4.5.0")

    implementation("net.logstash.logback:logstash-logback-encoder:8.1")
    implementation("ch.qos.logback.access:logback-access-tomcat:2.0.6")

    implementation("io.github.oshai:kotlin-logging-jvm:7.0.12")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    implementation(platform("org.junit:junit-bom:5.13.4"))
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.12.0")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.microsoft.playwright:playwright:1.54.0")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("io.ktor:ktor-client-core:3.2.3")
    implementation("io.ktor:ktor-client-cio:3.2.3") // CIO engine
    implementation("io.ktor:ktor-client-content-negotiation:3.2.3") // Content negotiation
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.2.3") // kotlinx serialization
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1-0.6.x-compat")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.10.2")
    implementation("org.reactivestreams:reactive-streams:1.0.4")

    implementation(platform("software.amazon.awssdk:bom:2.32.24"))
    implementation("software.amazon.awssdk:ses")
    implementation("software.amazon.awssdk:regions")

    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.commonmark:commonmark:0.25.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:6.0.0")
    implementation("org.unbescape:unbescape:1.1.6.RELEASE")

    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("ch.qos.logback:logback-core:1.5.18")
    implementation("commons-codec:commons-codec:1.19.0")

    downloadOnly("com.datadoghq:dd-java-agent:1.52.1")
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
            events("passed", "skipped", "failed", "started")
        }

        afterTest(
            KotlinClosure2<TestDescriptor, TestResult, Any>({ descriptor, result ->
                val className = descriptor.className ?: "UNKNOWN"
                val testName = descriptor.name
                val duration = result.endTime - result.startTime

                println("E2ETEST '$className.$testName' ${duration}ms")
            })
        )
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

    register("copyDownloadOnlyDeps", Copy::class) {
        from(downloadOnly)
        into(layout.buildDirectory.dir("download-only"))
        // remove version numbers from jar filenames
        rename(Pattern.compile("-([0-9]+[.]?)+.jar"), ".jar")
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
