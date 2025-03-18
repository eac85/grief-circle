package com.example.demo.controller

import com.example.demo.service.MermaidGeneratorService
import kotlinx.coroutines.runBlocking
import com.example.demo.service.SupabaseService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
@CrossOrigin(origins = ["*"])

@RestController
@RequestMapping("api/form")

class FormController(val supabaseService: SupabaseService) {
    @PostMapping("/consent")
    fun submitConsent(@RequestBody requestData: Map<String, Any>): ResponseEntity<String> {
        
        println("Received form data: $requestData")

        val name = requestData["name"] as? String ?: ""
        val email = requestData["email"] as? String ?: ""
        val anonymous = requestData["anonymous"] as? Boolean ?: false
    
        // Call your Supabase service
        runBlocking {
            val responseMessage = supabaseService.submitConsent(name, email, anonymous)
            println(responseMessage) // Should print "Form submitted successfully" if the request succeeds
        }
        return ResponseEntity.ok("Form submitted successfully!")


    }

}
