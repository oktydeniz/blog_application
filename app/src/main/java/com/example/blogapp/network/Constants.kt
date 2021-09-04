package com.example.blogapp.network

class Constants {

    companion object {
        private const val mainURL = "http://192.168.1.63:8000"
        private const val homeURL = "$mainURL/api"
        const val loginURL = "$homeURL/login"
        const val registerURL = "$homeURL/register"
        const val saveUserInfoURL = "$homeURL/saveUserInfo"
        const val posts = "$homeURL/posts"
        const val  createPost = "$homeURL/posts/create"
        const val  updatePost = "$homeURL/posts/update"
        const val  deletePost = "$homeURL/posts/delete"
        const val  likePost = "$homeURL/posts/likes"
        const val  comments = "$homeURL/posts/comments"

    }

}