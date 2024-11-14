plugins {
    id("java")
}

group = "com.programacion"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val lwjglVersion = "3.3.4"
val lwjglNatives = "natives-windows"

dependencies {
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-opengl")

    //Carga de textura
    implementation ("org.lwjgl:lwjgl-stb:3.3.4")
    implementation ("org.lwjgl:lwjgl-stb:3.3.4:natives-windows")

    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
}
