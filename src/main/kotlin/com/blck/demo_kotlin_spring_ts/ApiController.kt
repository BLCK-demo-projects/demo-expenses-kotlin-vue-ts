package com.blck.demo_kotlin_spring_ts

import com.blck.demo_kotlin_spring_ts.DB.Category
import com.blck.demo_kotlin_spring_ts.DB.CategoryDTO
import com.blck.demo_kotlin_spring_ts.DB.CategoryRepository
import com.blck.demo_kotlin_spring_ts.DB.ExpenseRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ApiController @Autowired constructor (
    private val categoryRepository: CategoryRepository,
    private val expenseRepository: ExpenseRepository
) {

    @PostMapping("/categories")
    fun addCategory(@RequestBody dto: CategoryDTO): ResponseEntity<Any> {
        if (categoryRepository.findByName(dto.name).isPresent) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Category already exists: " + dto.name)
        }
        val category = Category(dto)
        val saved = categoryRepository.save(category)
        return ResponseEntity.status(HttpStatus.CREATED).body(saved)
    }

    @GetMapping("/categories")
    fun getAllCategories(): ResponseEntity<List<CategoryDTO>> {
        val allCategories = categoryRepository.findAll()
            .map { category -> CategoryDTO(category.getName() ?: "" ) }
        return ResponseEntity.ok(allCategories)
    }

}
