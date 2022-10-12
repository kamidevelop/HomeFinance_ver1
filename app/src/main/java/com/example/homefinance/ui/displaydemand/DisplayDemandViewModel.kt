package com.example.homefinance.ui.displaydemand

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.homefinance.HomeFinanceDatabase
import com.example.homefinance.HomeFinanceDemand
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DisplayDemandViewModel(application: Application) : AndroidViewModel(application) {
    private val _demands = MutableLiveData<List<HomeFinanceDemand>>().apply {
        value = HomeFinanceDatabase.readDemandsDataByDate(getApplication(), SimpleDateFormat("dd-MM-yyyy").format(Date()))
    }

    val demands : LiveData<List<HomeFinanceDemand>> = _demands

    fun dataChanged(date : String){
        _demands.value = HomeFinanceDatabase.readDemandsDataByDate(getApplication(),date)
    }
}