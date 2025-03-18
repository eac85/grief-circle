package com.example.demo.service

import io.github.cdimascio.dotenv.dotenv
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import org.springframework.stereotype.Service

@Service
class SupabaseService {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json() // This will automatically install the JSON serialization plugin
        }
    }

    // Create and configure the Json object
    private val json = Json { prettyPrint = true }

    // Data class to hold consent data
    @Serializable
    data class ConsentFormData(val name: String, val email: String, val anonymous: Boolean)

    suspend fun submitConsent(name: String, email: String, anonymous: Boolean): String {
        val dotenv = dotenv()

        val SUPABASE_URL =
                dotenv["SUPABASE_URL"] ?: throw IllegalStateException("SUPABASE_URL not set!")
        val SUPABASE_KEY =
                dotenv["SUPABASE_KEY"] ?: throw IllegalStateException("SUPABASE_KEY not set!")
        val SENDGRID_API_KEY = dotenv["SENDGRID_API_KEY"]

        // Create a ConsentFormData object
        val emailBody =
                """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Consent Form</title>
        </head>
        <body style="font-family: Arial, sans-serif; background-color: #E8E0D2; margin: 0; padding: 20px; text-align: center;">
            <div style="background: white; padding: 20px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); max-width: 400px; margin: auto;">
                <p>thank you for being part of this project. </p>
                <p style="font-size: 12px; color: #555;">By submitting this form, you have given permission to share your photo and thoughts as part of this project.</p>
            </div>
        </body>
        </html>
    """.trimIndent() // Removes leading indentation

        val consentData = ConsentFormData(name, email, anonymous)

        // Serialize the data into JSON format
        val jsonData = json.encodeToString(consentData)

        // Log the data (for debugging purposes)
        println("Sending consent data: $jsonData")

        // Send data using Ktor client
        println(SUPABASE_KEY)
        val response =
                client.post("$SUPABASE_URL/rest/v1/consent") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $SUPABASE_KEY")
                        append("apikey", SUPABASE_KEY)
                        append(
                                HttpHeaders.ContentType,
                                ContentType.Application.Json.toString()
                        ) // Correct usage of content type
                    }
                    setBody(jsonData) // Setting the body to the serialized JSON
                }
        if (response.status.value in 200..299) {
            val sendGridService = SendGridService(SENDGRID_API_KEY)
            val result = sendGridService.sendEmail(email, "thank you", emailBody)
            println(result)
        }
        // Check response and return appropriate message
        return if (response.status.value in 200..299) {
            "Form submitted successfully"
        } else {
            "Form submission failed with status ${response.status.value}"
        }
    }
}
