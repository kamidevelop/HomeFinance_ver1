package com.example.homefinance.ui.displayincome

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.homefinance.HomeFinanceDatabase
import com.example.homefinance.HomeFinanceIncome
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DisplayIncomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _incomes = MutableLiveData<List<HomeFinanceIncome>>().apply {
        value = HomeFinanceDatabase.readIncomesDataByDate(getApplication(), SimpleDateFormat("dd-MM-yyyy").format(Date()))
    }

    val incomes : LiveData<List<HomeFinanceIncome>> = _incomes

    fun dataChanged(date : String){
        _incomes.value = HomeFinanceDatabase.readIncomesDataByDate(getApplication(),date)
    }
}