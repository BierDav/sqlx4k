package io.github.smyrgeorge.sqlx4k.publish

import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.plugins.signing.SigningExtension
import java.net.URI
import java.util.Properties

@Suppress("unused")
class PublishConventions : Plugin<Project> {

    private val descriptions: Map<String, String> = mapOf(
        "sqlx4k" to "A high-performance Kotlin Native database driver for PostgreSQL, MySQL, and SQLite.",
        "sqlx4k-codegen" to "A high-performance Kotlin Native database driver for PostgreSQL, MySQL, and SQLite.",
        "sqlx4k-mysql" to "A high-performance Kotlin Native database driver for MySQL.",
        "sqlx4k-postgres" to "A high-performance Kotlin Native database driver for PostgreSQL.",
        "sqlx4k-postgres-pgmq" to "A pgmq client using PostgreSQL as a message queue.",
        "sqlx4k-sqlite" to "A high-performance Kotlin Native database driver for SQLite.",
    )

    override fun apply(project: Project) {
        project.plugins.apply("com.vanniktech.maven.publish")
        project.plugins.apply("signing")
        project.extensions.configure<MavenPublishBaseExtension> {
            // Source publishing is always enabled by the Kotlin Multiplatform plugin.
            configure(
                KotlinMultiplatform(
                    // Whether to publish a 'sources' jar.
                    sourcesJar = true,
                )
            )
            coordinates(
                groupId = project.group as String,
                artifactId = project.name,
                version = project.version as String
            )

            pom {
                name.set(project.name)
                description.set(descriptions[project.name] ?: error("Missing description for $project.name"))
                url.set("https://github.com/smyrgeorge/sqlx4k")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/smyrgeorge/sqlx4k/blob/main/LICENSE")
                    }
                }

                developers {
                    developer {
                        id.set("smyrgeorge")
                        name.set("Yorgos S.")
                        email.set("smyrgoerge@gmail.com")
                        url.set("https://smyrgeorge.github.io/")
                    }
                }

                scm {
                    url.set("https://github.com/smyrgeorge/sqlx4k")
                    connection.set("scm:git:https://github.com/smyrgeorge/sqlx4k.git")
                    developerConnection.set("scm:git:git@github.com:smyrgeorge/sqlx4k.git")
                }
            }

            // Configure publishing to Maven Central

            // Enable GPG signing for all publications
            // signAllPublications()
        }
        val localProperties = Properties()
        val localPropertiesFile = project.rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { stream -> localProperties.load(stream) }
        }

        project.extensions.configure<SigningExtension> {
            useInMemoryPgpKeys(
                localProperties.getProperty("signingInMemoryKeyId"),
                localProperties.getProperty("signingInMemoryKey"),
                localProperties.getProperty("signingInMemoryKeyPassword"),
            )
        }
        project.extensions.configure<PublishingExtension> {
            repositories {
                maven {
                    name = "Github"
                    url = URI.create("https://maven.pkg.github.com/BierDav/sqlx4k")
                    credentials {
                        username = localProperties.getProperty("githubActor")
                        password = localProperties.getProperty("githubToken")
                    }
                }
            }
        }
    }
}
