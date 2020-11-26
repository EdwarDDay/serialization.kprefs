/*
 * Copyright 2020 Eduard Wolf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.edwardday.serialization.kprefs.setup

import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.BintrayPlugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import java.text.SimpleDateFormat
import java.util.*

private const val PUBLICATION_NAME = "release"

fun Project.configurePublish(sourcesTask: Jar, javadocTask: Jar) {
    apply<MavenPublishPlugin>()
    apply<BintrayPlugin>()

    val versionName = properties["VERSION_NAME"]!!.toString()

    extensions.configure<PublishingExtension>(PublishingExtension.NAME) {
        configureMaven(
            project = this@configurePublish,
            versionName = versionName,
            sourcesTask = sourcesTask,
            javadocTask = javadocTask
        )
    }

    extensions.configure<BintrayExtension>("bintray") {
        configureBintray(versionName = versionName)
    }
}

private fun PublishingExtension.configureMaven(
    project: Project,
    versionName: String,
    sourcesTask: Jar,
    javadocTask: Jar
) {
    project.afterEvaluate {
        publications {
            create(PUBLICATION_NAME, MavenPublication::class) {
                configureReleasePublication(project, versionName, sourcesTask, javadocTask)
            }
        }
    }
}

private fun MavenPublication.configureReleasePublication(
    project: Project,
    versionName: String,
    sourcesTask: Jar,
    javadocTask: Jar
) {
    from(project.components["release"])

    artifact(sourcesTask)
    artifact(javadocTask)

    groupId = "net.edwardday.serialization"
    artifactId = "kprefs"
    version = versionName

    pom {
        name.set("Kotlinx.serialization Android SharedPreferences format")
        packaging = "aar"
        description.set("A serialization format for Android SharedPreferences")
        url.set("https://github.com/EdwarDDay/serialization.kprefs")
        scm {
            url.set("https://github.com/EdwarDDay/serialization.kprefs")
            connection.set("scm:git:git://github.com/EdwarDDay/serialization.kprefs.git")
            developerConnection.set("scm:git:ssh://github.com/EdwarDDay/serialization.kprefs.git")
            tag.set("HEAD")
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

private fun BintrayExtension.configureBintray(
    versionName: String
) {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_KEY")
    setPublications(PUBLICATION_NAME)

    val bintrayFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ", Locale.ROOT)

    with(pkg) {
        repo = "maven"
        name = "net.edwardday.serialization:kprefs"
        setLicenses("Apache-2.0")
        vcsUrl = "https://github.com/EdwarDDay/serialization.kprefs.git"

        with(version) {
            name = versionName
            desc = "serialization kprefs $versionName release"
            released = bintrayFormat.format(Date())
            vcsTag = System.getenv("VCS_TAG")
        }
    }
}
