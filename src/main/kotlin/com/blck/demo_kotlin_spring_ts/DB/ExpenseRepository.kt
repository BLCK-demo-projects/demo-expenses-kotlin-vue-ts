package com.blck.demo_kotlin_spring_ts.DB

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ExpenseRepository : JpaRepository<Expense, UUID> {
    fun findByName(name: String): List<Expense>
}