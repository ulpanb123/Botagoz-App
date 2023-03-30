package com.example.visionin.data.network


import com.example.visionin.domain.AnswerResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MainApi {
    @Multipart
    @POST("answer_question/")
    suspend fun answerQuestion(
        @Part("question") question: RequestBody,
        @Part("user_id") userId: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<AnswerResponse>
}