package com.example.demo.service

import org.springframework.stereotype.Service
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

@Service
class MermaidGeneratorService {
    fun getClassDiagram(classText: String): String {
        val classRegex = """class (\w+)""".toRegex()
    val propRegex = """val (\w+): (\w+)""".toRegex()

    val diagram = StringBuilder("classDiagram\n")

    // Find all class definitions
    val classes = classRegex.findAll(classText).map { it.groupValues[1] }.toList()

    for (className in classes) {
        diagram.append("  class $className {\n") // Proper indentation

        // Find properties **belonging to this class**
        val classStartIndex = classText.indexOf("class $className")
        val nextClassIndex = classText.indexOf("class ", classStartIndex + 1)
        val classBody = if (nextClassIndex != -1) {
            classText.substring(classStartIndex, nextClassIndex)
        } else {
            classText.substring(classStartIndex)
        }

        val properties = propRegex.findAll(classBody).map { it.groupValues[1] to it.groupValues[2] }

        for ((name, type) in properties) {
            diagram.append("    $name: $type\n") // Correct indentation
        }
        
        diagram.append("  }\n") // Close the class definition
    }

    return diagram.toString()
    }

    
}
