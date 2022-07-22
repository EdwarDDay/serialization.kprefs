// SPDX-FileCopyrightText: 2020-2022 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

import org.gradle.jvm.tasks.Jar

plugins {
    `maven-publish`
    signing
}

signing {
    val signingKeyId: String? by project
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
}

afterEvaluate {
    publishing {
        publications {
            val releasePublication = create("release", MavenPublication::class) { configurePublication() }
            signing.sign(releasePublication)
            create("snapshot", MavenPublication::class) { configurePublication() }
        }

        repositories {
            maven {
                name = "MavenCentral"
                setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                configureCredentials()
            }
            maven {
                name = "MavenCentralSnapshots"
                setUrl("https://oss.sonatype.org/content/repositories/snapshots/")
                configureCredentials()
            }
        }
    }
}

fun MavenPublication.configurePublication() {
    from(components["release"])
    artifact(getSourcesArtifactTask())
    artifact(getJavaDocArtifactTask())
    groupId = "net.edwardday.serialization"
    artifactId = "kprefs"
    version = properties["VERSION_NAME"]!!.toString()

    pom {
        name.set("Kotlinx.serialization Android SharedPreferences format")
        packaging = "aar"
        description.set("A serialization format for Android SharedPreferences")
        url.set("https://github.com/EdwarDDay/serialization.kprefs")
        scm {
            url.set("https://github.com/EdwarDDay/serialization.kprefs")
            connection.set("scm:git:git://github.com/EdwarDDay/serialization.kprefs.git")
            developerConnection.set("scm:git:ssh://github.com/EdwarDDay/serialization.kprefs.git")
            tag.set(System.getenv("VCS_TAG"))
        }
        licenses {
            license {
                name.set("The Apache Software License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("EdwarDDay")
                name.set("Eduard Wolf")
            }
        }
    }
}

fun getSourcesArtifactTask(): Task {
    return tasks.filterIsInstance<Jar>()
        .single {
            it.name.contains("releaseSources", ignoreCase = true) &&
                it.archiveClassifier.get() == "sources"
        }
}

fun getJavaDocArtifactTask(): Task {
    return tasks.filterIsInstance<Jar>()
        .single {
            it.name.contains("javadoc", ignoreCase = true) &&
                it.archiveClassifier.get() == "javadoc"
        }
}

fun MavenArtifactRepository.configureCredentials() {
    credentials {
        val sonatypeNexusUsername: String? by project
        val sonatypeNexusPassword: String? by project
        username = sonatypeNexusUsername
        password = sonatypeNexusPassword
    }
}
