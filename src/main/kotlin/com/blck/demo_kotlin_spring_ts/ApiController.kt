package com.blck.demo_kotlin_spring_ts

import com.blck.demo_kotlin_spring_ts.DB.*
import com.blck.demo_kotlin_spring_ts.Exceptions.CategoryNotFoundException
import com.blck.demo_kotlin_spring_ts.ReponseDTOs.ExpenseSumByCategory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.*
import java.util.Optional

@RestController
class ApiController @Autowired constructor (
    private val categoryRepository: CategoryRepository,
    private val expenseRepository: ExpenseRepository
) {

    @Operation(summary = "Create a new category")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Success",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = Category::class))]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Category already exists",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    @PostMapping("/categories")
    fun addCategory(@RequestBody dto: CategoryDTO): ResponseEntity<Any> {
        if (categoryRepository.findByName(dto.name).isPresent) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Category already exists: ${dto.name}")
        }
        val category = Category(dto)
        val saved = categoryRepository.save(category)
        return ResponseEntity.status(HttpStatus.CREATED).body(saved)
    }

    @Operation(summary = "Get all category names")
    @ApiResponses(
        value = [ApiResponse(responseCode = "200", description = "Success")]
    )
    @GetMapping("/categories")
    fun getAllCategories(): ResponseEntity<List<CategoryDTO>> {
        val allCategories = categoryRepository.findAll()
            .map { category -> CategoryDTO(category.getName() ?: "" ) }
        return ResponseEntity.ok(allCategories)
    }

    @Operation(summary = "Delete a category and all associated expenses")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(
                responseCode = "404",
                description = "Category not found",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    @DeleteMapping("/categories/{name}")
    fun deleteCategory(@PathVariable name: String): ResponseEntity<Void> {
        val category: Optional<Category> = categoryRepository.findByName(name)
        if (category.isEmpty)
            return ResponseEntity.notFound().build()
        categoryRepository.delete(category.get())
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "Add a new expense to a category")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = Expense::class))]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Expense already exists",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Category not found",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    @PostMapping("/expenses")
    fun addExpense(@RequestBody expenseDTO: ExpenseDTO): ResponseEntity<Any> {
        val category = categoryRepository.findByName(expenseDTO.categoryFK)
            .orElseThrow { CategoryNotFoundException(expenseDTO.categoryFK) }

        val expenseExists = category.expenses.any {
            e -> expenseDTO.name.equals(e.getName(), ignoreCase = true)
        }
        if (expenseExists) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Expense already exists: ${expenseDTO.name}")
        }

        val expense: Expense = Expense(expenseDTO, category)
        val result: Expense = expenseRepository.save(expense)
        return ResponseEntity.ok(result)
    }

    @Operation(summary = "Get all expenses and their categories")
    @ApiResponses(
        value = [ApiResponse(responseCode = "200", description = "Success")]
    )
    @GetMapping("/expenses")
    fun getAllExpenses(): ResponseEntity<List<ExpenseDTO>> {
        val allExpenses = expenseRepository.findAll()
            .map { expense ->
                ExpenseDTO(
                    expense.getName()!!,
                    expense.getAmount()!!,
                    expense.getDate()!!,
                    expense.getCategoryFK()!!.getName()!!
                )
            }
        return ResponseEntity.ok(allExpenses)
    }

    @Operation(summary = "Get total amount ever spent")
    @ApiResponses(
        value = [ApiResponse(responseCode = "200", description = "Success")]
    )
    @GetMapping("/summary/total-spent")
    fun getTotalSpentAmount(): ResponseEntity<Double> {
        return ResponseEntity.ok(expenseRepository.getTotalSpent())
    }

    @Operation(summary = "Get a summary of spending per category")
    @ApiResponses(
        value = [ApiResponse(responseCode = "200", description = "Success")]
    )
    @GetMapping("/summary/spent-by-category")
    fun getSpentByCategory(): ResponseEntity<List<ExpenseSumByCategory>> {
        return ResponseEntity.ok(expenseRepository.getSpentByCategory())
    }

}
