plugins {
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    //implementation("com.github.ajalt:clikt:2.8.0")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(24)
}
application {
    mainClass.set("MainKt")
}

tasks.named<JavaExec>("run") {
    // Устанавливаем рабочий каталог - корень проекта
    workingDir = rootDir
    // Устанавливаем кодировку UTF-8 для Windows
    jvmArgs = listOf(
        "-Dfile.encoding=UTF-8",
        "-Dconsole.encoding=UTF-8",
        "-Dsun.stdout.encoding=UTF-8",
        "-Dsun.stderr.encoding=UTF-8"
    )
    // Устанавливаем переменные окружения
    environment("JAVA_TOOL_OPTIONS", "-Dfile.encoding=UTF-8")
}

// Для всех Java задач
tasks.withType<JavaExec> {
    systemProperty("file.encoding", "UTF-8")
    systemProperty("console.encoding", "UTF-8")
}
