package com.fityatra.app.ai

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

data class ChatMessage(
    val role: String,
    val content: String
)

private data class ApiRequest(
    val model: String,
    @SerializedName("max_tokens") val maxTokens: Int,
    val system: String,
    val messages: List<ApiMessage>
)

private data class ApiMessage(
    val role: String,
    val content: String
)

private data class ApiResponse(
    val content: List<ContentBlock>?,
    val error: ApiError?
)

private data class ContentBlock(
    val type: String,
    val text: String?
)

private data class ApiError(
    val type: String,
    val message: String
)

class ClaudeAiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val json = "application/json".toMediaType()

    fun sendMessage(
        apiKey: String,
        systemPrompt: String,
        history: List<ChatMessage>,
        userMessage: String
    ): Result<String> {
        if (apiKey.isBlank()) {
            return Result.failure(IllegalStateException("Claude API key is not set. Please add it in Settings."))
        }

        val messages = (history + ChatMessage("user", userMessage))
            .map { ApiMessage(it.role, it.content) }

        val requestBody = ApiRequest(
            model = "claude-haiku-4-5-20251001",
            maxTokens = 2048,
            system = systemPrompt,
            messages = messages
        )

        val request = Request.Builder()
            .url("https://api.anthropic.com/v1/messages")
            .addHeader("x-api-key", apiKey)
            .addHeader("anthropic-version", "2023-06-01")
            .addHeader("content-type", "application/json")
            .post(gson.toJson(requestBody).toRequestBody(json))
            .build()

        return try {
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""
            if (response.isSuccessful) {
                val parsed = gson.fromJson(body, ApiResponse::class.java)
                val text = parsed.content?.firstOrNull { it.type == "text" }?.text
                    ?: return Result.failure(Exception("Empty response from AI"))
                Result.success(text)
            } else {
                val parsed = runCatching { gson.fromJson(body, ApiResponse::class.java) }.getOrNull()
                val msg = parsed?.error?.message ?: "HTTP ${response.code}: $body"
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
