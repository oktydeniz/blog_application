package com.example.blogapp.model

import java.io.Serializable

data class Post(
    var id: Int,
    var likes: Int,
    var comments: Int,
    var date: String,
    var desc: String,
    var photo: String,
    var user: User,
    var selfLike: Boolean
):Serializable
