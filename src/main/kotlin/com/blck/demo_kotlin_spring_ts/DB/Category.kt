package com.blck.demo_kotlin_spring_ts.DB

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import java.util.*

@Entity
class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private var id: UUID? = null

    private var name: String? = null

    @OneToMany(mappedBy = "categoryFK", cascade = [CascadeType.REMOVE], fetch = FetchType.LAZY)
    @JsonManagedReference
    var expenses: MutableSet<Expense> = mutableSetOf()

    constructor()

    constructor(categoryDTO: CategoryDTO) {
        this.name = categoryDTO.name
    }

    fun getId(): UUID? {
        return id
    }

    fun setId(id: UUID?) {
        this.id = id
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }
}

