package com.example.demo.service

import com.sendgrid.SendGrid
import com.sendgrid.Request
import com.sendgrid.Response
import com.sendgrid.Method
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Email
import com.sendgrid.helpers.mail.objects.Content
import com.sendgrid.*
import java.io.IOException

class SendGridService(private val apiKey: String) {
    private val sendGridClient: SendGrid = SendGrid(apiKey)  // Instantiate SendGrid client once

    fun sendEmail(toEmail: String, subject: String, body: String): String {
        val fromEmail = Email("emery@emerychew.com") // Your email here
        val to = Email(toEmail)
        val content = Content("text/html", body)
        val mail = Mail(fromEmail, subject, to, content)

        try {
            val request = Request()
            request.method = Method.POST
            request.endpoint = "mail/send"
            request.body = mail.build()
            val response: Response = sendGridClient.api(request)

            // Check if the response is a success (200 or 202 status codes)
            return if (response.statusCode in 200..299) {
                "Email sent successfully. Status Code: ${response.statusCode}"
            } else {
                "Email failed with status ${response.statusCode}. Response: ${response.body}"
            }
        } catch (e: Exception) {
            // Catch any exceptions during the request
            println("Error sending email: ${e.message}")
            return "Email submission failed due to an exception: ${e.message}"
        }
    }
}

