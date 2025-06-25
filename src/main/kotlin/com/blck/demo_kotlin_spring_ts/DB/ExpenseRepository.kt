package com.blck.demo_kotlin_spring_ts.DB

import com.blck.demo_kotlin_spring_ts.ReponseDTOs.ExpenseSumByCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface ExpenseRepository : JpaRepository<Expense, UUID> {
    fun findByName(name: String): List<Expense>

    @Query("SELECT SUM(e.amount) FROM Expense e")
    fun getTotalSpent(): Double

    @Query("""
        SELECT new com.blck.demo_kotlin_spring_ts.ReponseDTOs.ExpenseSumByCategory(c.name, COALESCE(SUM(e.amount), 0))
        FROM Category c
        LEFT JOIN c.expenses e
        GROUP BY c.name
    """)
    fun getSpentByCategory(): List<ExpenseSumByCategory>
}