package com.example.homefinance.ui.family

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.example.homefinance.HomeFinanceDatabase
import com.example.homefinance.HomeFinancePerson

class FamilyViewModel(application: Application) : AndroidViewModel(application) {
    //private val _text = MutableLiveData<String>().apply {
      // value = "This is home Fragment"
    //}
    //val text: LiveData<String> = _text
    private val _persons = MutableLiveData<List<HomeFinancePerson>>().apply {
        value = HomeFinanceDatabase.readFamilyData(this@FamilyViewModel.getApplication())
    }

    val infoFamily : LiveData<List<HomeFinancePerson>> = _persons

    fun dataChanged(){
        _persons.value = HomeFinanceDatabase.readFamilyData(this@FamilyViewModel.getApplication())
    }

}