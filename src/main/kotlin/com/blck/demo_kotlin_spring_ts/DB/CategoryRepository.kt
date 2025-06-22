package com.blck.demo_kotlin_spring_ts.DB

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface CategoryRepository : JpaRepository<Category, UUID> {
    fun findByName(name: String): Optional<Category>
}