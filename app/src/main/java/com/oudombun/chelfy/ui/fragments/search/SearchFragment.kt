package com.oudombun.chelfy.ui.fragments.search


import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.oudombun.chelfy.R
import com.oudombun.chelfy.util.OnSearchActionListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_search.affordableLayout
import kotlinx.android.synthetic.main.fragment_search.beefLayout
import kotlinx.android.synthetic.main.fragment_search.chickenLayout
import kotlinx.android.synthetic.main.fragment_search.dessertLayout
import kotlinx.android.synthetic.main.fragment_search.easyDinnerLayout
import kotlinx.android.synthetic.main.fragment_search.fishLayout
import kotlinx.android.synthetic.main.fragment_search.searchView
import kotlinx.android.synthetic.main.fragment_search.toolbarBack


@AndroidEntryPoint
class SearchFragment : Fragment(), OnSearchActionListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbarBack.setOnClickListener {
            hideKeyboardFrom()
            findNavController().popBackStack()
        }

        searchView.initSearchListener(this)

        val itemClickListener = View.OnClickListener { clickedView ->
            predefinedSearchClick(clickedView)
        }

        easyDinnerLayout.setOnClickListener(itemClickListener)
        beefLayout.setOnClickListener(itemClickListener)
        fishLayout.setOnClickListener(itemClickListener)
        chickenLayout.setOnClickListener(itemClickListener)
        affordableLayout.setOnClickListener(itemClickListener)
        dessertLayout.setOnClickListener(itemClickListener)
    }


    override fun onSearchClicked(keyword: String) {
        actionSearch(keyword)
    }

    private fun actionSearch(searchTerm: String) {
        hideKeyboardFrom()
        val action =
            SearchFragmentDirections.actionSearchFragmentToSearchResultFragment(searchTerm)
        findNavController().navigate(action)

//        val fragment = SearchResultFragment.newInstance(searchTerm)
//        requireActivity().supportFragmentManager
//            .beginTransaction()
//            .replace(R.id.navHostFragment, fragment) // Replace 'fragment_container' with your container ID
//            .addToBackStack(null)  // Optional: Add to back stack for back navigation
//            .commit()
    }

    private fun hideKeyboardFrom() {
        val imm: InputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun backFragment() {
        requireActivity().supportFragmentManager.popBackStackImmediate()
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

        if (searchTerm.isNotEmpty()) {
            actionSearch(searchTerm)
        }
    }

}