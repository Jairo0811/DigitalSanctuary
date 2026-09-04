package com.jairomatias.digitalsanctuary.ai

import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GeminiAiAssistantTest {
    private lateinit var server: MockWebServer

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `proxy success is returned and prompt contains instruction and context`() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"text\":\"Concise summary\"}")
        )
        val assistant = GeminiAiAssistant(
            client = OkHttpClient(),
            proxyUrl = server.url("/ai").toString(),
            apiKey = ""
        )

        val result = assistant.ask("Summarize this passage", "Knowledge is preserved through careful reading.")

        val success = result as AiResult.Success
        assertEquals("Concise summary", success.text)
        val requestBody = server.takeRequest().body.readUtf8()
        assertTrue(requestBody.contains("Summarize this passage"))
        assertTrue(requestBody.contains("Knowledge is preserved through careful reading."))
    }

    @Test
    fun `proxy HTTP error is converted into AiResult Error`() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(503).setBody("unavailable"))
        val assistant = GeminiAiAssistant(
            client = OkHttpClient(),
            proxyUrl = server.url("/ai").toString(),
            apiKey = ""
        )

        val result = assistant.ask("Explain", "context")

        val error = result as AiResult.Error
        assertTrue(error.message.contains("503"))
    }
}
