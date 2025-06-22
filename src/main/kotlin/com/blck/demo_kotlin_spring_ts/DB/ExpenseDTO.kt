package com.blck.demo_kotlin_spring_ts.DB

import java.util.Date

data class ExpenseDTO(
    val name: String,
    val amount: Double,
    val date: Date,
    val categoryFK: String
)