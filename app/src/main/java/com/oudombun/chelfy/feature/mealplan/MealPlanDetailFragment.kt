package com.oudombun.chelfy.feature.mealplan

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navArgs
import com.oudombun.chelfy.R
import com.oudombun.chelfy.databinding.FragmentMealPlanDetailBinding
import com.oudombun.chelfy.feature.detail.DetailsActivityArgs
class MealPlanDetailFragment : Fragment() {

    private val args by navArgs<MealPlanDetailFragmentArgs>()

    private var _binding: FragmentMealPlanDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealPlanDetailBinding.inflate(inflater, container, false)

        binding.instructionsWebView.webViewClient = object : WebViewClient() {}
        val websiteUrl: String = args.sourceUrl
        binding.instructionsWebView.loadUrl(websiteUrl)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarBackResult.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}