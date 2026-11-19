package com.oudombun.chelfy.feature.search


import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.oudombun.chelfy.R
import com.oudombun.chelfy.databinding.FragmentSearchBinding
import com.oudombun.chelfy.core.ui.OnSearchActionListener
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class SearchFragment : Fragment(), OnSearchActionListener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarBack.setOnClickListener {
            hideKeyboardFrom()
            findNavController().popBackStack()
        }

        binding.searchView.initSearchListener(this)

        val itemClickListener = View.OnClickListener { clickedView ->
            predefinedSearchClick(clickedView)
        }

        binding.easyDinnerLayout.setOnClickListener(itemClickListener)
        binding.beefLayout.setOnClickListener(itemClickListener)
        binding.fishLayout.setOnClickListener(itemClickListener)
        binding.chickenLayout.setOnClickListener(itemClickListener)
        binding.affordableLayout.setOnClickListener(itemClickListener)
        binding.dessertLayout.setOnClickListener(itemClickListener)
    }

    override fun onSearchClicked(keyword: String) {
        actionSearch(keyword)
    }

    private fun actionSearch(searchTerm: String) {
        hideKeyboardFrom()
        val action = SearchFragmentDirections.actionSearchFragmentToSearchResultFragment(searchTerm)
        findNavController().navigate(action)
    }

    private fun hideKeyboardFrom() {
        val imm = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    fun predefinedSearchClick(view: View) {
        val searchTerm: String = when (view.id) {
            R.id.easyDinnerLayout -> "Easy Dinner"
            R.id.beefLayout -> "Beef"
            R.id.chickenLayout -> "Chicken"
            R.id.fishLayout -> "Fish"
            R.id.affordableLayout -> "Affordable"
            R.id.dessertLayout -> "Dessert"
            else -> ""
        }
        if (searchTerm.isNotEmpty()) actionSearch(searchTerm)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}