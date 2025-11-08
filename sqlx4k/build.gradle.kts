plugins {
    id("io.github.smyrgeorge.sqlx4k.multiplatform.lib")
    id("io.github.smyrgeorge.sqlx4k.publish")
    id("io.github.smyrgeorge.sqlx4k.dokka")
}

kotlin {
    compilerOptions {
        optIn.add("kotlinx.coroutines.ExperimentalCoroutinesApi")
        optIn.add("kotlin.concurrent.atomics.ExperimentalAtomicApi")
    }

    sourceSets {
        all {
            languageSettings.enableLanguageFeature("NestedTypeAliases")
            languageSettings.enableLanguageFeature("ContextParameters")
        }
        configureEach {
            languageSettings.progressiveMode = true
        }
        commonMain {
            dependencies {
                api(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.datetime)
                implementation(libs.kotlinx.io.core)
                implementation(libs.stately.concurrency.collections)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.assertk)
            }
        }
    }
}
