package com.example.homefinance.ui.infofamily

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.homefinance.R

class InfoFamilyFragment : Fragment() {

    companion object {
        fun newInstance() = InfoFamilyFragment()
    }

    private lateinit var viewModel: InfoFamilyViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_info_family, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(InfoFamilyViewModel::class.java)
        // TODO: Use the ViewModel
    }

}