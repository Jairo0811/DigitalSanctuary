package com.example.ai

import com.jairomatias.digitalsanctuary.BuildConfig
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

sealed interface AiResult {
    data class Success(val text: String) : AiResult
    data class Error(val message: String) : AiResult
}

interface AiAssistant {
    suspend fun ask(instruction: String, context: String): AiResult
}

class GeminiAiAssistant(
    private val client: OkHttpClient = OkHttpClient()
) : AiAssistant {
    override suspend fun ask(instruction: String, context: String): AiResult = withContext(Dispatchers.IO) {
        val prompt = buildString {
            appendLine("You are Digital Sanctuary, a concise reading and knowledge assistant.")
            appendLine("Instruction: $instruction")
            appendLine("Context:")
            append(context.take(MAX_CONTEXT_CHARS))
        }

        try {
            when {
                BuildConfig.AI_PROXY_URL.isNotBlank() -> callProxy(prompt)
                BuildConfig.GEMINI_API_KEY.isNotBlank() -> callGemini(prompt)
                else -> AiResult.Error(
                    "AI is not configured. Set AI_PROXY_URL (recommended) or GEMINI_API_KEY for local development."
                )
            }
        } catch (error: Exception) {
            AiResult.Error(error.message ?: "AI request failed")
        }
    }

    private fun callProxy(prompt: String): AiResult {
        val payload = JSONObject().put("prompt", prompt).toString()
        val request = Request.Builder()
            .url(BuildConfig.AI_PROXY_URL)
            .post(payload.toRequestBody(JSON_MEDIA_TYPE))
            .build()
        client.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) throw IOException("AI proxy returned HTTP ${response.code}")
            val json = JSONObject(body)
            val text = json.optString("text").ifBlank { json.optString("output") }
            return if (text.isBlank()) AiResult.Error("AI proxy returned an empty response") else AiResult.Success(text)
        }
    }

    private fun callGemini(prompt: String): AiResult {
        val parts = JSONArray().put(JSONObject().put("text", prompt))
        val contents = JSONArray().put(JSONObject().put("parts", parts))
        val payload = JSONObject().put("contents", contents).toString()
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${BuildConfig.GEMINI_API_KEY}"
        val request = Request.Builder()
            .url(url)
            .post(payload.toRequestBody(JSON_MEDIA_TYPE))
            .build()
        client.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) throw IOException("Gemini returned HTTP ${response.code}")
            val json = JSONObject(body)
            val text = json.optJSONArray("candidates")
                ?.optJSONObject(0)
                ?.optJSONObject("content")
                ?.optJSONArray("parts")
                ?.optJSONObject(0)
                ?.optString("text")
                .orEmpty()
            return if (text.isBlank()) AiResult.Error("Gemini returned an empty response") else AiResult.Success(text)
        }
    }

    private companion object {
        const val MAX_CONTEXT_CHARS = 24_000
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}
