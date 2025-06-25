package com.blck.demo_kotlin_spring_ts.Exceptions

class CategoryNotFoundException(categoryName: String) : RuntimeException(
    "Category not found: $categoryName"
)