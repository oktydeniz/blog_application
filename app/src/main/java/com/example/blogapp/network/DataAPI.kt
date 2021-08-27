package com.example.blogapp.network

import com.example.blogapp.model.Post

interface DataAPI {

    suspend fun getPosts(): List<Post>
}