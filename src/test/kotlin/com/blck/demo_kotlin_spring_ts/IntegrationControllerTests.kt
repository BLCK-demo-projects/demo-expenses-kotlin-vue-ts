package com.blck.demo_kotlin_spring_ts

import com.blck.demo_kotlin_spring_ts.DB.CategoryDTO
import com.blck.demo_kotlin_spring_ts.DB.CategoryRepository
import com.blck.demo_kotlin_spring_ts.DB.Expense
import com.blck.demo_kotlin_spring_ts.DB.ExpenseDTO
import com.blck.demo_kotlin_spring_ts.ReponseDTOs.ExpenseSumByCategory
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationControllerTests {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @BeforeEach
    fun setUp() {
        categoryRepository.deleteAll()
    }

    @Test
    fun addCategoryAlreadyExists() {
        val dto: CategoryDTO = CategoryDTO("Bills")

        restTemplate.postForEntity("/categories", dto, String::class.java)
        val response: ResponseEntity<String> = restTemplate.postForEntity("/categories", dto, String::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.CONFLICT)
    }

    @Test
    fun getAllCategories() {
        restTemplate.postForEntity("/categories", CategoryDTO("Bills"), String::class.java)
        restTemplate.postForEntity("/categories", CategoryDTO("Clothes"), String::class.java)

        val response: ResponseEntity<Array<CategoryDTO>> = restTemplate.getForEntity("/categories", Array<CategoryDTO>::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val categoryNames = response.body?.map {
            it.name
        }
        assertThat(categoryNames).containsExactlyInAnyOrder("Bills", "Clothes")
    }

    @Test
    fun getAllCategoriesEmpty() {
        val response: ResponseEntity<Array<CategoryDTO>> = restTemplate.getForEntity("/categories", Array<CategoryDTO>::class)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK);
        assertThat(response.body)
            .isNullOrEmpty();
    }

    @Test
    fun deleteCategory() {
        restTemplate.postForEntity("/categories", CategoryDTO("Bills"), String::class.java)

        val response: ResponseEntity<Void> = restTemplate.exchange("/categories/Bills", HttpMethod.DELETE, null, Void::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun deleteCategoryDeletesAssociatedExpenses() {
        restTemplate.postForEntity("/categories", CategoryDTO("Bills"), String::class.java)
        restTemplate.postForEntity("/expenses", ExpenseDTO("Water", 1.0, Date(), "Bills"), String::class.java)
        restTemplate.postForEntity("/expenses", ExpenseDTO("Heating", 1.0, Date(), "Bills"), String::class.java)

        val response: ResponseEntity<Void> = restTemplate.exchange("/categories/Bills", HttpMethod.DELETE, null, Void::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val expenses: ResponseEntity<Array<ExpenseDTO>> = restTemplate.getForEntity("/expenses", Array<ExpenseDTO>::class.java)
        assertThat(expenses.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(expenses.body)
            .isNullOrEmpty()
    }

    @Test
    fun deleteCategoryNotFound() {
        val response: ResponseEntity<Void> = restTemplate.exchange("/categories/Bills", HttpMethod.DELETE, null, Void::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun addExpense() {
        restTemplate.postForEntity("/categories", CategoryDTO("Bills"), String::class.java)
        val dto = ExpenseDTO("Water", 1.0, Date(), "Bills")

        val response: ResponseEntity<Expense> = restTemplate.postForEntity("/expenses", dto, Expense::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body)
            .isNotNull
        assertThat(response.body?.getName()).isEqualTo("Water")
    }

    @Test
    fun addExpenseMissingCategory() {
        val dto = ExpenseDTO("Water", 1.0, Date(), "Bills")

        val response: ResponseEntity<String> = restTemplate.postForEntity("/expenses", dto, String::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(response.body)
            .isNotNull
            .isEqualTo("Category not found: Bills")
    }

    @Test
    fun addExpenseAlreadyExists() {
        restTemplate.postForEntity("/categories", CategoryDTO("Bills"), String::class.java)
        val dto = ExpenseDTO("Water", 1.0, Date(), "Bills")

        restTemplate.postForEntity("/expenses", dto, Expense::class.java)
        val response: ResponseEntity<String> = restTemplate.postForEntity("/expenses", dto, String::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.CONFLICT)
    }

    @Test
    fun getTotalSpentAmount() {
        restTemplate.postForEntity("/categories", CategoryDTO("Bills"), String::class.java)
        restTemplate.postForEntity("/categories", CategoryDTO("Clothes"), String::class.java)
        restTemplate.postForEntity("/expenses", ExpenseDTO("Water", 10.0, Date(), "Bills"), Expense::class.java)
        restTemplate.postForEntity("/expenses", ExpenseDTO("Shows", 50.0, Date(), "Clothes"), Expense::class.java)

        val response: ResponseEntity<Double> = restTemplate.getForEntity("/summary/total-spent", Double::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body)
            .isNotNull
            .isEqualTo(60.0)
    }

    @Test
    fun getSpentByCategory() {
        restTemplate.postForEntity("/categories", CategoryDTO("Bills"), String::class.java)
        restTemplate.postForEntity("/categories", CategoryDTO("Clothes"), String::class.java)
        restTemplate.postForEntity("/expenses", ExpenseDTO("Water", 10.0, Date(), "Bills"), Expense::class.java)
        restTemplate.postForEntity("/expenses", ExpenseDTO("Shoes", 50.0, Date(), "Clothes"), Expense::class.java)

        val response: ResponseEntity<Array<ExpenseSumByCategory>> = restTemplate.getForEntity("/summary/spent-by-category", Array<ExpenseSumByCategory>::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body)
            .isNotNull
            .extracting(ExpenseSumByCategory::categoryFK, ExpenseSumByCategory::sumOfExpenses)
            .containsExactlyInAnyOrder(
                tuple("Bills", 10.0),
                tuple("Clothes", 50.0)
            )
    }

}