package com.example.visionin.presentation.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.visionin.R

class QuestionAnswerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var questionTextView : TextView
    private var answerTextView : TextView
    private lateinit var loader : ProgressBar

    init {
        val view = inflate(context, R.layout.item_question_answer, this)

        questionTextView = view.findViewById(R.id.tv_question)
        answerTextView = view.findViewById(R.id.tv_answer)
        loader = view.findViewById(R.id.spin_kit)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.QuestionAnswerView,
            0, 0
        ).apply {
            try{
                questionTextView.text = this.getString(R.styleable.QuestionAnswerView_question)
                answerTextView.text = this.getString(R.styleable.QuestionAnswerView_answer)
                loader.visibility =
                    if(this.getBoolean(R.styleable.QuestionAnswerView_loading, true))
                        View.VISIBLE
                    else
                        View.INVISIBLE
            } finally {
                recycle()
            }
        }
    }
}