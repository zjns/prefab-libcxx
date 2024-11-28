plugins {
    alias(libs.plugins.lsplugin.publish)
}

val androidTargetSdkVersion by extra(35)
val androidMinSdkVersion by extra(21)
val androidCompileSdkVersion by extra(35)
val androidNdkVersion by extra("26.3.11579264")
val androidCmakeVersion by extra("3.22.1+")
