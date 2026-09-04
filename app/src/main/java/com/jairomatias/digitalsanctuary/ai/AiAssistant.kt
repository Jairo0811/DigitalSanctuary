package com.jairomatias.digitalsanctuary.ai

import com.jairomatias.digitalsanctuary.BuildConfig
import java.io.IOException
import java.util.concurrent.TimeUnit
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
    private val client: OkHttpClient = defaultClient(),
    private val proxyUrl: String = BuildConfig.AI_PROXY_URL,
    private val apiKey: String = BuildConfig.GEMINI_API_KEY,
    private val geminiEndpoint: String = DEFAULT_GEMINI_ENDPOINT
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
                proxyUrl.isNotBlank() -> callProxy(prompt)
                apiKey.isNotBlank() -> callGemini(prompt)
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
            .url(proxyUrl)
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
        val separator = if (geminiEndpoint.contains('?')) '&' else '?'
        val url = "$geminiEndpoint${separator}key=$apiKey"
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
        const val DEFAULT_GEMINI_ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

        fun defaultClient(): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
            .build()
    }
}
