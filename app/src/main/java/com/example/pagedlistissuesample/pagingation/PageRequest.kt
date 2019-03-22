package com.example.pagedlistissuesample.pagingation

enum class PaginationType {
    AFTER, BEFORE
}

data class PageRequest(val pageSize: Int, val cursor: String, val paginationType: PaginationType)