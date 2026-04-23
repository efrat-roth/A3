plugins {
    id("java")
}

group = "org"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // SLF4J API
    implementation("org.slf4j:slf4j-api:2.0.9")

    // Log4j2 Core and API
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")

    // SLF4J to Log4j2 Bridge (Routes SLF4J calls to Log4j2)
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.20.0")
}

tasks.test {
    useJUnitPlatform()
}