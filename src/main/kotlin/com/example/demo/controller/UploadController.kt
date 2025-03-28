package com.example.demo.controller

import com.example.demo.service.SupabaseService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.MultipartFile
import java.io.File
import kotlinx.coroutines.runBlocking


@CrossOrigin(origins = ["*"])
@RestController
@RequestMapping("api/upload")
class UploadController(val supabaseService: SupabaseService) {

    @PostMapping("/file")
    suspend fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<String> {
        val fileName = file.originalFilename ?: "default_file_name"
            val tempFile = File.createTempFile("upload_", fileName)

        runBlocking {
            val res = supabaseService.listFilesInBucket()
            val responseMessage = supabaseService.uploadFileToSupabase(tempFile) // Call the suspend function
            println(responseMessage) // Should print "Form submitted successfully" if the request succeeds
        }
        return ResponseEntity.ok("Form submitted successfully!")
    }
}
