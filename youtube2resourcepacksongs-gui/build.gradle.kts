import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version ("2.0.4")
}

tasks.withType<ShadowJar> {
    archiveName = "$baseName-$version.$extension"
    manifest {
        attributes(mapOf("Main-Class" to "com.github.shynixn.youtube2resourcepacksongs.gui.MainKt"))
    }
}

publishing {
    publications {
        (findByName("mavenJava") as MavenPublication).artifact(tasks.findByName("shadowJar")!!)
    }
}

dependencies {
    implementation(project(":youtube2resourcepacksongs-api"))
    implementation(project(":youtube2resourcepacksongs-logic"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
    implementation("commons-cli:commons-cli:1.4")
}