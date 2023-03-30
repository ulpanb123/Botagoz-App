package com.example.visionin.presentation.main

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visionin.data.network.RetrofitBuilder
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File

class MainFragmentViewmodel : ViewModel() {
    private val userId: Int = 1
    var question: String = ""
    var image: Bitmap? = null

    private val _answerLiveData: MutableLiveData<String> = MutableLiveData()
    val answerLiveData: LiveData<String> = _answerLiveData


    suspend fun startQna(question: String, userId: Int, image: Bitmap) {
        val questionBody = question.toRequestBody("text/plain".toMediaTypeOrNull())
        val userIdBody = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        // Create a RequestBody from the bitmap
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        val imageBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray)
        val multipartBody = MultipartBody.Part.createFormData("image", "image.jpg", imageBody)

        viewModelScope.launch {
            val answer =
                RetrofitBuilder.apiService.answerQuestion(questionBody, userIdBody, multipartBody)
                    .body()?.answer ?: "Null"
            _answerLiveData.postValue(answer)
        }

    }
}
