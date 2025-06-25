package com.blck.demo_kotlin_spring_ts.ReponseDTOs

data class ExpenseSumByCategory(
    val categoryFK: String,
    val sumOfExpenses: Double
)
