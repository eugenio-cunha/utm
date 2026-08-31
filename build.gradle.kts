// Top-level build file where you can add configuration options common to all sub-projects/modules.
// These `apply false` declarations resolve the real plugin implementations onto the
// build classpath so the gnss.* convention plugins in build-logic (which apply
// them by id) can load; the versions/config they use are owned by build-logic.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.b256.root)
}
