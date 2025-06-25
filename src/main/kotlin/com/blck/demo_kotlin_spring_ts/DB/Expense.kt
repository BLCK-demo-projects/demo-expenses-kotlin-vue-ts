package com.blck.demo_kotlin_spring_ts.DB

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.util.*

@Entity
class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private var id: UUID? = null

    private var amount: Double? = null

    private var name: String? = null

    private var date: Date? = null

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "category_id")
    private var categoryFK: Category? = null

    constructor()

    constructor(expenseDTO: ExpenseDTO, category: Category) {
        setAmount(expenseDTO.amount)
        setName(expenseDTO.name)
        setDate(expenseDTO.date)
        setCategoryFK(category)
    }

    fun setId(id: UUID?) {
        this.id = id
    }

    fun getId(): UUID? {
        return id
    }

    fun setAmount(amount: Double?) {
        this.amount = amount
    }

    fun getAmount(): Double? {
        return amount
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getName(): String? {
        return name
    }

    fun setDate(date: Date?) {
        this.date = date
    }

    fun getDate(): Date? {
        return date
    }

    fun setCategoryFK(category: Category?) {
        categoryFK = category
    }

    fun getCategoryFK(): Category? {
        return categoryFK
    }
}