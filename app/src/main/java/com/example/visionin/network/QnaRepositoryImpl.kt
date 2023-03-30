package com.example.visionin.data.network

import com.example.visionin.domain.QnaRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class QnaRepositoryImpl(private val mainApi: MainApi) : QnaRepository {
    override suspend fun startQna(question: String, userId: Int, image: String): String {
        val questionBody = question.toRequestBody("text/plain".toMediaTypeOrNull())
        val userIdBody = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val imageFile = File(image)
        val imageBody = imageFile.asRequestBody("image/jpeg".toMediaType())
        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, imageBody)

        return mainApi.answerQuestion(questionBody, userIdBody, imagePart).body()!!.answer
    }

}