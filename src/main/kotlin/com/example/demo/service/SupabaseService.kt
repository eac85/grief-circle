package com.example.demo.service

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.plugins.logging.* // Import Logging plugin
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.springframework.stereotype.Service
import java.io.File
 
@Service
class SupabaseService {

    val SUPABASE_URL = System.getenv("SUPABASE_URL")
    val SUPABASE_KEY = System.getenv("SUPABASE_KEY")
    val SENDGRID_API_KEY = System.getenv("SENDGRID_API_KEY")

    private val ktorClient = HttpClient {
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
            <body style="font-family: Ubuntu Sans Mono; background-color: #E8E0D2; margin: 0; padding: 20px; text-align: center;">
                <div style="background: white; padding: 20px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); max-width: 400px; margin: auto;">
                <p>Thank you for being part of this project.</p>
                <p style="font-size: 12px; color: #555;">By submitting this form, you have given permission to share your photo and thoughts as part of this project.</p>
                <p style="font-size: 14px; color:rgb(28, 30, 33);">
                    <a href="https://grief-circle-production.up.railway.app/about.html" target="_blank">Learn more about the project</a>
                </p>
                </div>
            </body>
            </html>
            """.trimIndent()
        
        val consentData = ConsentFormData(name, email, anonymous)

        // Serialize the data into JSON format
        val jsonData = json.encodeToString(consentData)

        // Log the data (for debugging purposes)
        println("Sending consent data: $jsonData")

        val response =
                ktorClient.post("$SUPABASE_URL/rest/v1/consent") {
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

    suspend fun uploadFileToSupabase(file: File) {
        val fileName = file.name ?: "uploaded_file"
        val storage_url =
                SUPABASE_URL + "/storage/v1/object/photos" // Replace with your Supabase URL
        val url = "$storage_url/$fileName"
        val client = HttpClient(CIO)

        try {
            val fileBytes = file.readBytes()

            // Create the request body from the file's byte array
            val response: HttpResponse =
                    client.post(url) {
                        headers {
                            append(HttpHeaders.Authorization, "Bearer $SUPABASE_KEY")
                            append("apikey", SUPABASE_KEY)
                            append(
                                    HttpHeaders.ContentType,
                                    ContentType.Application.OctetStream.toString()
                            )
                        }
                        setBody(fileBytes)
                    }

            if (response.status.value in 200..299) {
                println("File uploaded successfully.")
            } else {
                println("Upload failed: ${response.status}")
                println("Response body: ${response.bodyAsText()}")
            }
        } catch (e: Exception) {
            println("Error uploading file: ${e.localizedMessage}")
        } finally {
            client.close()
        }
    }

    suspend fun listFilesInBucket(): List<Pair<String, Int>> {
        val tableUrl = "$SUPABASE_URL/rest/v1/uploads?select=url,id"
        val client = HttpClient(CIO)

        return try {
            val response: HttpResponse = client.get(tableUrl) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $SUPABASE_KEY")
                    append("apikey", SUPABASE_KEY)
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
            }

            if (response.status.value in 200..299) {
                // Parse the JSON response to extract file URLs and IDs
                val files = Json.decodeFromString<List<FileMetadata>>(response.bodyAsText())
                files.map { file ->
                    Pair(file.url, file.id) // Return a list of pairs (url, id)
                }
            } else {
                println("Failed to list files: ${response.status}")
                println("Response body: ${response.bodyAsText()}")
                emptyList()
            }
        } catch (e: Exception) {
            println("Error listing files: ${e.localizedMessage}")
            emptyList()
        } finally {
            client.close()
        }
    }
    
    @Serializable
    data class FileMetadata(val url: String, val id: Int)

    @Serializable
    data class FileObject(val name: String)
}

