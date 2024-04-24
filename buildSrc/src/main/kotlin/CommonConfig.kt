import org.cadixdev.gradle.licenser.LicenseExtension
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.the
import org.gradle.plugins.ide.idea.model.IdeaModel

fun Project.applyCommonConfiguration() {
    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral {
            mavenContent {
                releasesOnly()
            }
        }
        maven { url = uri("https://maven.enginehub.org/repo/") }
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
            mavenContent {
                snapshotsOnly()
            }
        }
    }

    configurations.all {
        resolutionStrategy {
            cacheChangingModulesFor(5, "MINUTES")
        }
    }

    plugins.withId("java") {
        the<JavaPluginExtension>().toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    dependencies {
        constraints {
            for (conf in configurations) {
                if (conf.isCanBeConsumed || conf.isCanBeResolved) {
                    // dependencies don't get declared in these
                    continue
                }
                add(conf.name, "com.google.guava:guava") {
                    version { require(Versions.GUAVA) }
                    because("Mojang provides Guava")
                }
                add(conf.name, "com.google.code.gson:gson") {
                    version { require(Versions.GSON) }
                    because("Mojang provides Gson")
                }
                add(conf.name, "it.unimi.dsi:fastutil") {
                    version { require(Versions.FAST_UTIL) }
                    because("Mojang provides FastUtil")
                }
            }
        }
    }

    apply(plugin = "org.cadixdev.licenser")
    configure<LicenseExtension> {
        header(rootProject.file("HEADER.txt"))
        include("**/*.java")
        include("**/*.kt")
    }

    plugins.withId("idea") {
        configure<IdeaModel> {
            module {
                isDownloadSources = true
                isDownloadJavadoc = true
            }
        }
    }
}
