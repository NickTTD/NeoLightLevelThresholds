plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

modGroup = "com.nickttd.neolightlevels"

tasks.named("runClient") {
    // Removed dependsOn("copyCoremodJar") as the task no longer exists
}

tasks.withType<Jar> {
    manifest {
        attributes(
            "FMLCorePluginContainsFMLMod" to true
        )
    }
}

dependencies {
    compileOnly(files("libs/TConstruct-1.7.10-1.8.8.jar"))
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
