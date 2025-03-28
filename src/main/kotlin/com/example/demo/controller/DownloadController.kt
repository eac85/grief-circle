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
@RequestMapping("api/download")
class DownloadController(val supabaseService: SupabaseService) {

    @GetMapping("/file") 
    suspend fun getFileList(): ResponseEntity<List<String>> {
        return try {
            val fileList = supabaseService.listFilesInBucket().map { it.first } // Extract the first element of each pair
            ResponseEntity.ok(fileList)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(emptyList())
        }
    }
}
