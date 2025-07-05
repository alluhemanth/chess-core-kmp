import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
    //id("org.jetbrains.dokka") version "2.0.0"
}

group = "io.github.alluhemanth"
version = "1.0.0"

kotlin {
    jvm()
    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                //put your multiplatform dependencies here
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

android {
    namespace = "io.github.alluhemanth.chess.core"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates(group.toString(), "chess-core", version.toString())

    pom {
        name = "chess-core-kmp"
        description = "A library."
        inceptionYear = "2025"
        url = "https://github.com/alluhemanth/chess-core-kmp/"
        licenses {
            license {
                name.set("The MIT License")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id = "alluhemanth"
                name = "Hemanth"
                url = "https://github.com/alluhemanth"
            }
        }
        scm {
            url.set("https://github.com/alluhemanth/chess-core-kmp")
            connection.set("scm:git:git://github.com/alluhemanth/chess-core-kmp.git")
            developerConnection.set("scm:git:ssh://github.com:alluhemanth/chess-core-kmp.git")
        }
    }
}

tasks.withType<Test>().configureEach {
    maxParallelForks = Runtime.getRuntime().availableProcessors()
    testLogging {
        events("passed", "failed")
    }
}

/*
dokka {
    moduleName.set("chess-core")
    dokkaPublications.html {
        suppressInheritedMembers.set(true)
        failOnWarning.set(true)
        outputDirectory.set(rootDir.resolve("build/dokka"))
    }

    dokkaSourceSets {
        configureEach {
            documentedVisibilities.set(
                setOf(
                    VisibilityModifier.Public,
                    VisibilityModifier.Internal,
                    VisibilityModifier.Protected,
                )
            )
        }

        named("commonMain") {
            includes.from("dokka/dokka.md")
        }
    }

    pluginsConfiguration.html {
        customStyleSheets.from("dokka/assets/custom.css")
        customAssets.from("dokka/assets/logo-icon.svg")
        footerMessage.set("chess-core")
    }

}
*/