plugins {
    alias(libs.plugins.agp.lib)
    alias(libs.plugins.lsplugin.publish)
    alias(libs.plugins.lsplugin.cmaker)
    `maven-publish`
    signing
}

val androidTargetSdkVersion: Int by rootProject.extra
val androidMinSdkVersion: Int by rootProject.extra
val androidBuildToolsVersion: String by rootProject.extra
val androidCompileSdkVersion: Int by rootProject.extra
val androidNdkVersion: String by rootProject.extra
val androidCmakeVersion: String by rootProject.extra


android {
    compileSdk = androidCompileSdkVersion
    ndkVersion = androidNdkVersion

    buildFeatures {
        buildConfig = false
        prefabPublishing = true
        androidResources = false
        prefab = true
    }

    packaging {
        jniLibs {
            excludes += "**.so"
        }
    }

    prefab {
        register("cxx") {
            headers = "jni/libcxx/include"
        }
    }

    defaultConfig {
        minSdk = androidMinSdkVersion
    }

    lint {
        abortOnError = true
        checkReleaseBuilds = false
    }

    externalNativeBuild {
        cmake {
            path = file("jni/CMakeLists.txt")
            version = androidCmakeVersion
        }
    }
    namespace = "io.github.zjns.libcxx"

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

cmaker {
    default {
        abiFilters("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        arguments += "-DANDROID_STL=none"
        arguments += "-DCMAKE_VERBOSE_MAKEFILE=ON"
    }
    buildTypes {
    }
}

publishing {
    repositories {
        maven {
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/zjns/prefab-libcxx")
            credentials {
                username = project.findProperty("gpr.user") as String?
                    ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("gpr.key") as String?
                    ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("libcxx") {
            artifactId = "libcxx"
            afterEvaluate {
                from(components.getByName("release"))
            }
            group = "io.github.zjns.libcxx"
            version = androidNdkVersion
            pom {
                name.set("libcxx")
                description.set("libcxx")
                url.set("https://github.com/zjns/prefab-libcxx")
                licenses {
                    license {
                        name.set("Apache v2.0")
                        url.set("https://github.com/llvm/llvm-project/blob/main/LICENSE.TXT")
                    }
                }
                developers {
                    developer {
                        name.set("LLVM")
                        url.set("https://llvm.org/")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/zjns/prefab-libcxx.git")
                    url.set("https://github.com/zjns/prefab-libcxx")
                }
            }
        }
    }
}
