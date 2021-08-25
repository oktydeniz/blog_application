package com.example.blogapp.model

data class Comment(
    val id: Int,
    val comment: String,
    val date: String,
    val user: User
)