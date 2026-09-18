package com.example.nlp

import com.example.data.model.ActionType
import com.example.data.model.TaskType

data class NlpResult(
    val replyText: String,
    val actionType: ActionType = ActionType.NONE,
    val actionPayload: String? = null,
    val taskType: TaskType? = null,
    val targetTimeMillis: Long = 0,
    val timeLabel: String = "",
    val dateLabel: String = "",
    val repeatInterval: String = "تاک",
    val requiresConfirmation: Boolean = false,
    val confirmationPrompt: String? = null,
    val isClarificationNeeded: Boolean = false,
    val agentType: String = "Agent ـی گشتی"
)

object KurdishNlpEngine {

    // Kurdish and Arabic numeral normalizer
    fun normalizeKurdishText(raw: String): String {
        return raw.trim()
            .replace('٠', '0')
            .replace('١', '1')
            .replace('٢', '2')
            .replace('٣', '3')
            .replace('٤', '4')
            .replace('٥', '5')
            .replace('٦', '6')
            .replace('٧', '7')
            .replace('٨', '8')
            .replace('٩', '9')
            .replace('ي', 'ی')
            .replace('ك', 'ک')
            .replace('ة', 'ە')
    }

    fun processCommand(input: String, conversationContext: String? = null): NlpResult {
        val text = normalizeKurdishText(input)
        val lower = text.lowercase()

        // 1. Clarification Follow-ups
        if (conversationContext?.contains("بەیانی یان", ignoreCase = true) == true ||
            conversationContext?.contains("بەیانی یان ئێوارە", ignoreCase = true) == true
        ) {
            val isMorning = lower.contains("بەیانی") || lower.contains("ڕۆژ") || lower.contains("am")
            val period = if (isMorning) "بەیانی" else "ئێوارە"
            return NlpResult(
                replyText = "زۆر باشە. زەنگەکەم بۆ کاتژمێر ٧ی $period بە سەرکەوتوویی ڕێکخست.",
                actionType = ActionType.ALARM,
                taskType = TaskType.ALARM,
                timeLabel = if (isMorning) "07:00 AM" else "07:00 PM",
                dateLabel = "سبەی",
                agentType = "Agent ـی مۆبایل"
            )
        }

        // 2. Sensitive Action Confirmation
        if (lower.contains("سڕینەوەی یادەوەری") || lower.contains("سڕینەوەی چات") || lower.contains("ریست") || lower.contains("reset")) {
            return NlpResult(
                replyText = "ئاگاداربە! ئەم کردارە هەموو تۆمارە هەڵگیراوەکان دەسڕێتەوە.",
                requiresConfirmation = true,
                confirmationPrompt = "دڵنیایت دەتەوێت ئەم کردارە هەستیارە جێبەجێ بکەم؟",
                actionType = ActionType.DEVICE_CONTROL,
                actionPayload = "CLEAR_DATA",
                agentType = "Agent ـی ئاسایش و داتا"
            )
        }

        // 3. Game & Car commands
        if (lower.contains("گەراج") || lower.contains("سەیارە") || lower.contains("tuning") || lower.contains("race") || lower.contains("drift") || lower.contains("موتۆر") || lower.contains("یاری")) {
            if (lower.contains("race ئامادە بکە") || lower.contains("ئامادەی بکە بۆ پێشبڕکێ") || lower.contains("setup race")) {
                return NlpResult(
                    replyText = "سیستەمی پێشنیارکراوی تایبەت بە پێشبڕکێ (Race Preset) ئامادەکرا:\n- مۆتۆر: ئاستی 4 بە بەهێزی بەرز\n- تۆربۆ: Twin Turbo\n- هایدرۆلیک/Suspension: Race نزمکراوە\n- تایەر: تایەری تایبەتی Racing Slick بە گریپی بەرز\n\nئایا پەسەندی دەکەیت ئەم تەعدیلاتە لە گەراج جێبەجێ بکرێت؟",
                    actionType = ActionType.CAR_TUNING,
                    actionPayload = "PRESET_RACE",
                    agentType = "Agent ـی سەیارە و میکانیک"
                )
            }
            if (lower.contains("drift") || lower.contains("درێفت")) {
                return NlpResult(
                    replyText = "کۆمەڵەی تایبەتی درێفت ئامادەکرا:\n- زاویەی تایەر/Camber: -3.5 پلە\n- هێزی ڕاگرتنی دواوە: دەستی کراوە\n- گێڕ: دەستی بە نیسبەتی خێرا\nدەتەوێت بچیتە گەراج بۆ پشکنینی ڕاستەوخۆ؟",
                    actionType = ActionType.CAR_TUNING,
                    actionPayload = "PRESET_DRIFT",
                    agentType = "Agent ـی سەیارە و میکانیک"
                )
            }
            if (lower.contains("موتۆر") && (lower.contains("upgrade") || lower.contains("بەهێز") || lower.contains("چاک"))) {
                return NlpResult(
                    replyText = "فەرمانی نوێکردنەوەی مۆتۆر وەرگیرا. هێزی سەیارەکەت +45 ئەسپ بەرز دەبێتەوە. دەتوانیت لە بەشی گەراج وردەکارییەکەی بە تەواوی ڕێکبخەیت.",
                    actionType = ActionType.CAR_TUNING,
                    actionPayload = "UPGRADE_ENGINE",
                    agentType = "Agent ـی سەیارە و میکانیک"
                )
            }
            if (lower.contains("ڕەنگ") || lower.contains("بۆیە")) {
                val color = if (lower.contains("سور")) "سوور" else if (lower.contains("شین")) "شین" else if (lower.contains("ڕەش")) "ڕەشی ماتی کاربۆن" else "میتالیک"
                return NlpResult(
                    replyText = "داواکاری گۆڕینی ڕەنگ بۆ $color تۆمارکرا. لە گەراج سەردانی تابلۆی ڕەنگ بکە بۆ پەسەندکردن.",
                    actionType = ActionType.CAR_TUNING,
                    actionPayload = "CHANGE_COLOR",
                    agentType = "Agent ـی دیزاینی سەیارە"
                )
            }
            if (lower.contains("بازاڕ") || lower.contains("هەرزان") || lower.contains("کڕین")) {
                return NlpResult(
                    replyText = "لە بازاڕی سەیارەکانی BASOKA گەڕام: سەیارەی Retro Falcon 1978 ئێستا بە نرخی گونجاوی ٢٢,٠٠٠ کریدت بەردەستە بە شێوازی کلاسیک بۆ بوژاندنەوە و restore.",
                    actionType = ActionType.CAR_PURCHASE,
                    actionPayload = "OPEN_MARKET",
                    agentType = "Agent ـی بازاڕ و مامەڵە"
                )
            }
        }

        // 4. Timer
        if (lower.contains("تایمەر") || lower.contains("timer")) {
            val minutes = extractNumber(lower) ?: 10
            if (lower.contains("بوەستێنە") || lower.contains("ڕاگرتن")) {
                return NlpResult(
                    replyText = "تایمەرەکە وەستێنرا.",
                    actionType = ActionType.TIMER,
                    actionPayload = "STOP_TIMER",
                    agentType = "Agent ـی مۆبایل"
                )
            }
            if (lower.contains("ڕیسێت") || lower.contains("سفر")) {
                return NlpResult(
                    replyText = "تایمەرەکە ڕیسێت کرایەوە سەر سفر.",
                    actionType = ActionType.TIMER,
                    actionPayload = "RESET_TIMER",
                    agentType = "Agent ـی مۆبایل"
                )
            }
            val targetTime = System.currentTimeMillis() + (minutes * 60 * 1000)
            return NlpResult(
                replyText = "باشە. تایمەرێکی $minutes خولەکیم بۆت چالاک کرد.",
                actionType = ActionType.TIMER,
                taskType = TaskType.TIMER,
                targetTimeMillis = targetTime,
                timeLabel = "$minutes خولەک",
                dateLabel = "ئەمڕۆ",
                agentType = "Agent ـی کات"
            )
        }

        // 5. Alarm
        if (lower.contains("زەنگ") || lower.contains("alarm")) {
            val num = extractNumber(lower)
            if (num == null) {
                return NlpResult(
                    replyText = "بە دڵنیایی. بۆ چ کاتێک زەنگەکە دابنێم؟",
                    isClarificationNeeded = true,
                    agentType = "Agent ـی کات"
                )
            }
            val hasPeriod = lower.contains("بەیانی") || lower.contains("ئێوارە") || lower.contains("نیوەڕۆ") || lower.contains("شەو")
            if (!hasPeriod && num in 1..12) {
                return NlpResult(
                    replyText = "باشە، سبەی کاتژمێر ${num}ی بەیانی یان ${num}ی ئێوارە؟",
                    isClarificationNeeded = true,
                    agentType = "Agent ـی کات"
                )
            }
            val period = if (lower.contains("ئێوارە") || lower.contains("شەو")) "ئێوارە" else "بەیانی"
            return NlpResult(
                replyText = "باشە. زەنگێک بۆ کاتژمێر ${num}ی $period دادەنێم.",
                actionType = ActionType.ALARM,
                taskType = TaskType.ALARM,
                timeLabel = "$num:00 $period",
                dateLabel = if (lower.contains("سبەی")) "سبەی" else "ئەمڕۆ",
                agentType = "Agent ـی مۆبایل"
            )
        }

        // 6. Reminder
        if (lower.contains("بیرم بخەرەوە") || lower.contains("یادم بخەرەوە") || lower.contains("ئاگادارم بکەرەوە") || lower.contains("reminder")) {
            val num = extractNumber(lower) ?: 8
            val isTomorrow = lower.contains("سبەی") || lower.contains("بەیانی")
            val isEveryDay = lower.contains("هەر ڕۆژ") || lower.contains("ڕۆژانە")
            val dateLabel = if (isEveryDay) "ڕۆژانە" else if (isTomorrow) "سبەی" else "ئەمڕۆ"
            val timeLabel = "$num:00"

            val cleanContent = input
                .replace("بیرم بخەرەوە", "")
                .replace("یادم بخەرەوە", "")
                .replace("سبەی", "")
                .replace("لە 8", "")
                .replace("لە ٨", "")
                .replace("کاتژمێر", "")
                .replace("کە", "")
                .trim()
            val finalSubject = if (cleanContent.isNotBlank()) cleanContent else "کارە دیاریکراوەکەت"

            val reply = if (isEveryDay) {
                "باشە. هەر ڕۆژ کاتژمێر ${num}ی بەیانی بیرت دەخەمەوە کە: $finalSubject."
            } else {
                "باشە. بۆ $dateLabel کاتژمێر $timeLabel بیرت دەخەمەوە: $finalSubject."
            }

            return NlpResult(
                replyText = reply,
                actionType = ActionType.REMINDER,
                taskType = TaskType.REMINDER,
                timeLabel = timeLabel,
                dateLabel = dateLabel,
                repeatInterval = if (isEveryDay) "ڕۆژانە" else "تاک",
                agentType = "Agent ـی یادەوەری"
            )
        }

        // 7. Calendar
        if (lower.contains("کۆبوونەوە") || lower.contains("calendar") || lower.contains("مەوعید") || lower.contains("دیدار")) {
            val num = extractNumber(lower) ?: 4
            return NlpResult(
                replyText = "کۆبوونەوەکە دیاریکرا بۆ سبەی کاتژمێر $num:00.\nدڵنیایت دەتەوێت ئەم کۆبوونەوەیە لە ڕۆژژمێر زیاد بکەم؟",
                actionType = ActionType.CALENDAR,
                taskType = TaskType.CALENDAR,
                timeLabel = "$num:00",
                dateLabel = "سبەی",
                requiresConfirmation = true,
                confirmationPrompt = "دڵنیایت دەتەوێت ئەم کۆبوونەوەیە لە ڕۆژژمێر زیاد بکەم؟",
                agentType = "Agent ـی پلاندانان"
            )
        }

        // 8. Device controls
        if (lower.contains("فلاش") || lower.contains("flash")) {
            return NlpResult(
                replyText = "ڕێنمایی جێبەجێکردنی فلاش: فلاشلایت لە ڕێگەی کۆنترۆڵی خێرای ئامێرەکەتەوە دەستی پێگەیشت.",
                actionType = ActionType.DEVICE_CONTROL,
                actionPayload = "FLASHLIGHT",
                agentType = "Agent ـی ئامێر"
            )
        }
        if (lower.contains("wi-fi") || lower.contains("وایفای") || lower.contains("wifi")) {
            return NlpResult(
                replyText = "بۆ پاراستنی ئاسایشی ئەندرۆید، ڕێکخستنی Wi-Fi لە پەڕەی فەرمی سیستەم دەکرێتەوە تا خۆت هەڵیبژێریت.",
                actionType = ActionType.DEVICE_CONTROL,
                actionPayload = "WIFI_SETTINGS",
                agentType = "Agent ـی ئامێر"
            )
        }
        if (lower.contains("کامێرا") || lower.contains("camera")) {
            return NlpResult(
                replyText = "کامێرا دەکرێتەوە بۆ وێنەگرتن یان شیکاری فایل و دەق بە مۆڵەتی ڕاستەوخۆ.",
                actionType = ActionType.DEVICE_CONTROL,
                actionPayload = "OPEN_CAMERA",
                agentType = "Agent ـی وێنە و بینین"
            )
        }
        if (lower.contains("settings") || lower.contains("ڕێکخستن")) {
            return NlpResult(
                replyText = "پەڕەی ڕێکخستنەکانی سیستەم کرایەوە.",
                actionType = ActionType.DEVICE_CONTROL,
                actionPayload = "OPEN_SETTINGS",
                agentType = "Agent ـی ئامێر"
            )
        }

        // 9. Summarization
        if (lower.contains("کورت") || lower.contains("پوختە") || lower.contains("شیکەرەوە") || lower.contains("خوێندنەوە")) {
            return NlpResult(
                replyText = "پوختەی ناوەڕۆکەکە ئامادەکرا:\n١. دەستنیشانکردنی بیرۆکەی سەرەکی بە شێوازێکی هاوسەنگ.\n٢. ڕوونکردنەوەی گرنگترین خاڵە کاریگەرەکان بە کوردیی سۆرانیی پاراو.\n٣. پوختەکردنی دەرەنجامە پراکتیکییەکان بۆ بڕیاردانی خێرا.",
                actionType = ActionType.FILE_ACTION,
                agentType = "Agent ـی شیکاری فایل و دەق"
            )
        }

        // 10. Study & Exam
        if (lower.contains("خوێندن") || lower.contains("تاقیکردنەوە") || lower.contains("وانە") || lower.contains("پلان")) {
            return NlpResult(
                replyText = "پلانی خوێندنی پێشنیارکراو بەپێی کاتی تۆ:\n- دانیشتنی ١ (٤٥ خولەک): تێگەیشتن لە چەمکە تیۆرییەکان\n- پشوودان (١٠ خولەک)\n- دانیشتنی ٢ (٤٠ خولەک): چارەسەرکردنی نموونە و پرسیاری وزاری\n- هەڵسەنگاندن (١٥ خولەک): دیاریکردنی خاڵە لاوازەکان",
                actionType = ActionType.NONE,
                agentType = "Agent ـی فێرکاری و خوێندن"
            )
        }

        // 11. Code & Android
        if (lower.contains("کۆد") || lower.contains("ئەپ") || lower.contains("android") || lower.contains("error") || lower.contains("kotlin")) {
            return NlpResult(
                replyText = "ئەمەش چارەسەری کۆد بە شێوەی دروست و مۆدێرنی Kotlin Jetpack Compose:\n\n```kotlin\n@Composable\nfun BasokaCustomWidget() {\n    Card(\n        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),\n        modifier = Modifier.fillMaxWidth().padding(16.dp)\n    ) {\n        Text(\n            text = \"BASOKA Smart Module\",\n            style = MaterialTheme.typography.titleMedium,\n            modifier = Modifier.padding(16.dp)\n        )\n    }\n}\n```\nهەڵەکە بەهۆی گۆڕانی جۆری ڕووکارەوە بوو کە بە سەلامەتی ڕێگری لە Crash دەکات.",
                actionType = ActionType.CODE_GEN,
                agentType = "Agent ـی پرۆگرامسازی و کۆد"
            )
        }

        // 12. Translation & Writing
        if (lower.contains("وەرگێڕ") || lower.contains("ئیمەیل") || lower.contains("نامە") || lower.contains("cv") || lower.contains("نوسین")) {
            return NlpResult(
                replyText = "دەقی فەرمی داواکراو ئامادەکرا:\n\nسڵاو و ڕێز،\nئەم نامەیە بۆ مەبەستی بەدواداچوونی فەرمی و پێشکەشکردنی داواکارییە. سوپاس بۆ کات و هاوکاریتان لە جێبەجێکردنی ئەم پڕۆژەیە بە سەرکەوتوویی.\n\nلەگەڵ ڕێزم،\nبەکارهێنەری BASOKA",
                actionType = ActionType.NONE,
                agentType = "Agent ـی نووسین و زمان"
            )
        }

        // 13. General intelligent default
        return NlpResult(
            replyText = "فەرمانت بە سەرکەوتوویی شی کرایەوە. BASOKA دەتوانێت لە بیرخستنەوە، زەنگ، کۆبوونەوە، گەراج و سەیارە، کۆدنووسین و ڕێکخستنی ڕۆژانەت هاوکاریت بکات. چ فرمانێکی ترت هەیە؟",
            actionType = ActionType.NONE,
            agentType = "Agent ـی گشتی"
        )
    }

    private fun extractNumber(str: String): Int? {
        val wordMap = mapOf(
            "یەک" to 1, "دوو" to 2, "سێ" to 3, "چوار" to 4, "پێنج" to 5,
            "شەش" to 6, "حەوت" to 7, "هەشت" to 8, "نۆ" to 9, "دە" to 10,
            "یازدە" to 11, "دوازدە" to 12, "بیست" to 20, "سی" to 30
        )
        for ((word, num) in wordMap) {
            if (str.contains(word)) return num
        }
        val match = Regex("\\b\\d+\\b").find(str)
        return match?.value?.toIntOrNull()
    }
}
