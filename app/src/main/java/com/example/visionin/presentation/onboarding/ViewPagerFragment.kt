package com.example.visionin.presentation.onboarding

import android.content.Context
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.viewpager2.widget.ViewPager2
import com.example.visionin.R
import com.example.visionin.presentation.onboarding.screens.FirstFragment
import com.example.visionin.presentation.onboarding.screens.FourthFragment
import com.example.visionin.presentation.onboarding.screens.SecondFragment
import com.example.visionin.presentation.onboarding.screens.ThirdFragment
import com.google.android.material.button.MaterialButton


class ViewPagerFragment : Fragment() {

    private lateinit var adapter: ViewPagerAdapter
    private lateinit var layoutOnboardingIndicator : LinearLayout
    private lateinit var buttonOnboardingAction : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_pager, container, false)

        val fragmentList = arrayListOf<Fragment>(
            FirstFragment(),
            SecondFragment(),
            ThirdFragment(),
            FourthFragment()
        )

        adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        layoutOnboardingIndicator = view.findViewById(R.id.layoutOnboardingIndicators)
        buttonOnboardingAction = view.findViewById(R.id.buttonOnBoardingAction)

        val viewpager = view.findViewById<ViewPager2>(R.id.vp_onboarding)
        viewpager.adapter = adapter

        setOnBoardingIndicator()
        setCurrentOnBoardingIndicators(0)

        viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentOnBoardingIndicators(position)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if(position == 2)
                    view.findViewById<ConstraintLayout>(R.id.fragment_view_pager).setBackgroundResource(R.color.bg_onboard_dark)
                else if(position == 1)
                    view.findViewById<ConstraintLayout>(R.id.fragment_view_pager).setBackgroundResource(R.color.bg_onboard_light)
            }
        })

        buttonOnboardingAction.setOnClickListener {
            if (viewpager.currentItem + 1 < adapter.itemCount) {
                viewpager.currentItem = viewpager.currentItem + 1
            } else {
                findNavController().navigate(R.id.action_viewPagerFragment_to_permissionsFragment)
            }
        }

        return view
    }

    private fun setOnBoardingIndicator() {
        val indicators = arrayOfNulls<ImageView>(adapter.itemCount)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(8, 0, 8, 0)
        }
        Log.e("Viewpager", context.toString())
        for (i in indicators.indices) {
            indicators[i] = ImageView(context).apply {
                setImageDrawable(ContextCompat.getDrawable(context, R.drawable.onboarding_indicator_inactive))
                setLayoutParams(layoutParams)
            }
            layoutOnboardingIndicator.addView(indicators[i])
            Log.e("Viewpager", indicators[i].toString())
        }
    }

    private fun setCurrentOnBoardingIndicators(index: Int) {
        val childCount = layoutOnboardingIndicator.childCount
        for (i in 0 until childCount) {
            val imageView = layoutOnboardingIndicator.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.onboarding_indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.onboarding_indicator_inactive
                    )
                )
            }
        }
        if (index == adapter.itemCount - 1) {
            buttonOnboardingAction.text = resources.getString(R.string.start)
        } else {
            buttonOnboardingAction.text = resources.getString(R.string.next)
        }
    }
}