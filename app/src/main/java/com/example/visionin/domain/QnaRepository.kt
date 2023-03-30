package com.example.visionin.domain

interface QnaRepository {
    suspend fun startQna(question : String, userId : Int, image : String) : String
}