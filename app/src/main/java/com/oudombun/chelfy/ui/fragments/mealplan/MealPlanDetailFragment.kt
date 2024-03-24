package com.oudombun.chelfy.ui.fragments.mealplan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import androidx.navigation.navArgs
import com.oudombun.chelfy.R
import com.oudombun.chelfy.ui.DetailsActivityArgs
import kotlinx.android.synthetic.main.fragment_instructions.view.*

class MealPlanDetailFragment : Fragment() {
    private val args by navArgs<MealPlanDetailFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_meal_plan_detail, container, false)

        view.instructions_webView.webViewClient = object : WebViewClient() {}
        val websiteUrl: String = args.sourceUrl
        view.instructions_webView.loadUrl(websiteUrl)

        return view
    }

}