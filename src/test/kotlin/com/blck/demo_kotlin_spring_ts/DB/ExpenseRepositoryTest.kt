package com.blck.demo_kotlin_spring_ts.DB

import com.blck.demo_kotlin_spring_ts.ReponseDTOs.ExpenseSumByCategory
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import java.text.SimpleDateFormat
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DataJpaTest
class ExpenseRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var expenseRepository: ExpenseRepository

    @BeforeEach
    fun setUp() {
        var categoryDTO: CategoryDTO = CategoryDTO("Bills")
        var category: Category = Category(categoryDTO)
        entityManager.persist(category)

        var date: Date = SimpleDateFormat("yyyy-MM-dd").parse("2024-01-1")
        var expenseDTO: ExpenseDTO = ExpenseDTO("Shoes", 10.0, date, "Bills")
        entityManager.persist(Expense(expenseDTO, category))

        categoryDTO = CategoryDTO("Shopping")
        category = Category(categoryDTO)
        entityManager.persist(category)

        date = SimpleDateFormat("yyyy-MM-dd").parse("2024-03-1")
        expenseDTO = ExpenseDTO("Shoes", 5.0, date, "Shopping")
        entityManager.persist(Expense(expenseDTO, category))
    }

    @Test
    fun findByName() {
        val expenses: List<Expense> = expenseRepository.findByName("Shoes")

        assertEquals("Shoes", expenses.first().getName())
    }

    @Test
    fun findByNameNotFound() {
        val expenses: List<Expense> = expenseRepository.findByName("")

        assertTrue(expenses.isEmpty())
    }

    @Test
    fun getTotalSpent() {
        assertEquals(15.0, expenseRepository.getTotalSpent())
    }

    @Test
    fun getSpentByCategory() {
        val spentByCategory: List<ExpenseSumByCategory> = expenseRepository.getSpentByCategory()

        assertAll(
            { assertEquals(2, spentByCategory.size) },
            { assertEquals(ExpenseSumByCategory("Bills", 10.0), spentByCategory.first()) },
            { assertEquals(ExpenseSumByCategory("Shopping", 5.0), spentByCategory.last()) }
        )
    }
}