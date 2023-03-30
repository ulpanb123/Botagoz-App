package com.example.visionin.presentation.main

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import com.example.visionin.R
import com.example.visionin.presentation.LauncherActivity
import com.example.visionin.presentation.utils.QuestionAnswerView
import com.github.ybq.android.spinkit.SpinKitView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*

private const val REQ_CODE_SPEECH_INPUT = 15

class MainFragment : Fragment() {

    private lateinit var viewModel: MainFragmentViewmodel


    private lateinit var ivImage : FragmentContainerView
    private lateinit var btnCapture : Button
    private lateinit var tvQuestion : TextView
    private lateinit var tvAnswer : TextView
    private lateinit var loader : SpinKitView
    private lateinit var cardQnA : QuestionAnswerView
    private var speaker : TextToSpeech? = null
    private lateinit var imageBitmap : Bitmap

    private var CAN_PROCEED = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        val childFragment = CameraFragment()

        // Begin a FragmentTransaction
        val transaction = childFragmentManager.beginTransaction()

        // Add the child fragment to the parent fragment
        transaction.add(R.id.image_container, childFragment)

        // Commit the FragmentTransaction
        transaction.commit()

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainFragmentViewmodel::class.java)

        viewModel.answerLiveData.observe(viewLifecycleOwner) {
            loader.visibility = View.GONE
            tvAnswer.visibility = View.VISIBLE
            tvAnswer.text = it

            speaker!!.speak(it, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivImage = view.findViewById(R.id.image_container)
        btnCapture = view.findViewById(R.id.capture_button)
        tvQuestion = view.findViewById(R.id.tv_question)
        tvAnswer = view.findViewById(R.id.tv_answer)
        loader = view.findViewById(R.id.spin_kit)

        cardQnA = view.findViewById(R.id.card_qna)

        speaker = TextToSpeech(
            context
        ) { status ->
            if (status != TextToSpeech.ERROR) {
                speaker?.language = Locale.US
            }
        }

        if(!hasCamera()) {
            btnCapture.isEnabled = false
        }

        btnCapture.setOnClickListener {
            val childFragment =
                childFragmentManager.findFragmentById(R.id.image_container)
            if(childFragment !is CameraFragment) {
                Log.e("MainFragment", "cast error")
                val newChildFragment = CameraFragment()

                // Begin a FragmentTransaction
                val transaction = childFragmentManager.beginTransaction()

                // Add the child fragment to the parent fragment
                transaction.replace(R.id.image_container, newChildFragment)
                transaction.addToBackStack(null)

                // Commit the FragmentTransaction
                transaction.commit()
            }

            val cameraFrag = childFragmentManager.findFragmentById(R.id.image_container) as CameraFragment
            cameraFrag.handleCaptureClick(it)
        }
    }

    private fun hasCamera(): Boolean {
        return requireActivity().packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
        REQ_CODE_SPEECH_INPUT -> {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                cardQnA.visibility = View.VISIBLE

                tvQuestion.text = result!![0]
                viewModel.question = tvQuestion.text.toString()
                tvAnswer.visibility = View.GONE
                loader.visibility = View.VISIBLE

                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.startQna(tvQuestion.text.toString(), 1, imageBitmap)
                }
            }
        }
        }
    }

    private fun promptSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US)
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(
                context,
                "Sorry! Your device doesn't support speech input",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    fun passBitmap(image: Bitmap) {
        Log.e("MainFragment", "ready" )
        imageBitmap = image
        val bitmapToSend = Bitmap.createScaledBitmap(imageBitmap, 224, 224, false)
        viewModel.image = bitmapToSend
        CAN_PROCEED = true
        promptSpeechInput()
    }

}