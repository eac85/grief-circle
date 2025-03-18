package com.example.demo.controller

import com.example.demo.service.MermaidGeneratorService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestParam

@RestController
@RequestMapping("/diagram")
class DiagramController(val mermaidGeneratorService: MermaidGeneratorService) {

    @GetMapping("/classDiagram")
    fun classDiagram(@RequestParam input: String): String {
        return mermaidGeneratorService.getClassDiagram(input)
    }

}
