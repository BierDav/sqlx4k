plugins {
    id("io.github.smyrgeorge.sqlx4k.multiplatform.lib")
    id("io.github.smyrgeorge.sqlx4k.publish")
    id("io.github.smyrgeorge.sqlx4k.dokka")
}

kotlin {
    @Suppress("unused")
    sourceSets {
        all {
            languageSettings.enableLanguageFeature("NestedTypeAliases")
            languageSettings.enableLanguageFeature("ContextParameters")
        }
        configureEach {
            languageSettings.progressiveMode = true
        }
        all {
            languageSettings.enableLanguageFeature("NestedTypeAliases")
            languageSettings.enableLanguageFeature("ContextParameters")
        }
        val commonMain by getting {
            dependencies {
                api(project(":sqlx4k"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.assertk)
                implementation(libs.kotlinx.io.core)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.sqlite.jdbc)
            }
        }
    }
}
