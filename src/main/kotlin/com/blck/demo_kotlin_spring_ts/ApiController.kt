package com.blck.demo_kotlin_spring_ts

import com.blck.demo_kotlin_spring_ts.DB.*
import com.blck.demo_kotlin_spring_ts.Exceptions.CategoryNotFoundException
import com.blck.demo_kotlin_spring_ts.ReponseDTOs.ExpenseSumByCategory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.Optional

@RestController
class ApiController @Autowired constructor (
    private val categoryRepository: CategoryRepository,
    private val expenseRepository: ExpenseRepository
) {

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

    @GetMapping("/categories")
    fun getAllCategories(): ResponseEntity<List<CategoryDTO>> {
        val allCategories = categoryRepository.findAll()
            .map { category -> CategoryDTO(category.getName() ?: "" ) }
        return ResponseEntity.ok(allCategories)
    }

    @DeleteMapping("/categories/{name}")
    fun deleteCategory(@PathVariable name: String): ResponseEntity<Void> {
        val category: Optional<Category> = categoryRepository.findByName(name)
        if (category.isEmpty)
            return ResponseEntity.notFound().build()
        categoryRepository.delete(category.get())
        return ResponseEntity.ok().build()
    }

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

    @GetMapping("/summary/total-spent")
    fun getTotalSpentAmount(): ResponseEntity<Double> {
        return ResponseEntity.ok(expenseRepository.getTotalSpent())
    }

    @GetMapping("/summary/spent-by-category")
    fun getSpentByCategory(): ResponseEntity<List<ExpenseSumByCategory>> {
        return ResponseEntity.ok(expenseRepository.getSpentByCategory())
    }

}
