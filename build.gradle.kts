plugins {
    application
    scala
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "scalatro"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.scala-lang:scala3-library_3:3.7.4")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("scalatro.ScalatroMain")
}

javafx {
    version = "26.0.1"
    modules("javafx.controls", "javafx.fxml")
}