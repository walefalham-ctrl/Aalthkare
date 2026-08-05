dependencies {
    // ... المكتبات الموجودة مسبقاً ...
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    // ... إلخ ...

    // 🔐 أضف هذا السطر فقط (مكتبة التشفير):
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
}
