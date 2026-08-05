dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // ... بقية المكتبات الموجودة ...

    // 🔐 أضف هذا السطر هنا:
    implementation("androidx.security:security-crypto:1.0.0")
}
