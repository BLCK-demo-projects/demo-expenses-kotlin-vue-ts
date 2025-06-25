package com.blck.demo_kotlin_spring_ts.DB

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import java.text.SimpleDateFormat
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

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
        val category: Optional<Category> = categoryRepository.findByName("Bills");

        assertTrue(category.isPresent);
        assertEquals("Bills", category.get().getName());
    }

    @Test
    fun findByNameNotFound() {
        val category: Optional<Category> = categoryRepository.findByName("");

        assertFalse(category.isPresent);
    }
}