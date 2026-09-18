package com.example.ai

import android.util.Log
import com.example.BuildConfig
import com.example.nlp.KurdishNlpEngine
import com.example.nlp.NlpResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun processUserPrompt(prompt: String, contextHistory: String? = null): NlpResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        // If no API key or placeholder key, use offline Kurdish NLP engine
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext KurdishNlpEngine.processCommand(prompt, contextHistory)
        }

        // Attempt Gemini API call
        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val systemPrompt = "تۆ BASOKA ـیت، یاریدەدەری زیرەکی کەسیی کوردی. وەڵامەکانت هەمیشە بە زمانی کوردیی سۆرانیی پاراو، ڕوون، پڕۆفیشناڵ و بەبێ هیچ هەڵەیەکی ڕێنووس بنووسە. ئەگەر بەکارهێنەر داوای زەنگ یان بیرخستنەوە یان کۆبوونەوەی کرد، وردەکارییەکان دەستنیشان بکە."

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", "$systemPrompt\n\nپرسیاری بەکارهێنەر: $prompt"))
                        })
                    })
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val respStr = response.body?.string()

            if (response.isSuccessful && !respStr.isNullOrBlank()) {
                val json = JSONObject(respStr)
                val candidates = json.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val content = firstCandidate?.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val text = parts?.optJSONObject(0)?.optString("text")

                if (!text.isNullOrBlank()) {
                    // Check if there are actionable intents in the response or prompt
                    val localIntent = KurdishNlpEngine.processCommand(prompt, contextHistory)
                    return@withContext localIntent.copy(replyText = text.trim())
                }
            }
        } catch (e: Exception) {
            Log.w("GeminiService", "Online API fallback to offline NLP: ${e.message}")
        }

        // Fallback to offline rule-based Kurdish NLP
        return@withContext KurdishNlpEngine.processCommand(prompt, contextHistory)
    }
}
