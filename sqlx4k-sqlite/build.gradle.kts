plugins {
    id("io.github.smyrgeorge.sqlx4k.multiplatform.lib")
    id("io.github.smyrgeorge.sqlx4k.publish")
    id("io.github.smyrgeorge.sqlx4k.dokka")
}

kotlin {
    jvm()
    sourceSets {
        configureEach {
            languageSettings.progressiveMode = true
        }
        commonMain {
            dependencies {
                api(project(":sqlx4k"))
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.assertk)
                implementation(libs.kotlinx.io.core)
            }
        }
        jvmMain {
            dependencies {
                implementation(libs.sqlite.jdbc)
            }
        }
//        wasmJsMain {
//            dependencies {
//                implementation(npm("@sqlite.org/sqlite-wasm","3.51.0-build1"))
//            }
//        }
    }
}
