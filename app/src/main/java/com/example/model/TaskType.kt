package com.example.model

enum class TaskType(val id: String, val title: String, val subtitle: String) {
    AZKAR(
        id = "azkar",
        title = "أذكار الصباح والمساء والنوم (حصن المسلم)",
        subtitle = "مع التهليل (تاج الذكر) 100 مرة"
    ),
    BAQARAH(
        id = "baqarah",
        title = "سورة البقرة (7 صفحات)",
        subtitle = "قراءة بتدبر لحفظ البركة"
    ),
    RUQYAH(
        id = "ruqyah",
        title = "آيات الشفاء والسكينة",
        subtitle = "قراءة أو سماع بتدبر وطمأنينة"
    ),
    SADAKAH(
        id = "sadakah",
        title = "الصدقة اليومية",
        subtitle = "ولو بمبلغ بسيط أو إطعام طعام"
    ),
    WIRD(
        id = "wird",
        title = "الورد اليومي (صفحتين)",
        subtitle = "قراءة صفحتين متتاليتين من القرآن"
    ),
    NAMES(
        id = "names",
        title = "مختصر فقه أسماء الله الحسنى",
        subtitle = "قراءة الدرس أو الاسم اليومي والتأمل"
    )
}
