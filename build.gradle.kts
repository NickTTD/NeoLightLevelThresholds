plugins {
    id("com.gtnewhorizons.gtnhconvention")
}
tasks.named("runClient") {
    // Rem./oved dependsOn("copyCoremodJar") as the task no longer exists
}

tasks.withType<Jar> {
    manifest {
        attributes(
            "FMLCorePluginContainsFMLMod" to true
        )
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-AoutRefMapFile=mixins.neohunger.refmap.json")
}

dependencies {

}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.legacydevmc.org/")
    }
    maven {
        url = uri("https://nexus.gtnewhorizons.com/repository/public/")
    }
}
