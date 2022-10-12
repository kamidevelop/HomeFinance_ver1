package com.example.homefinance.ui.targets

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.example.homefinance.HomeFinanceDatabase
import com.example.homefinance.HomeFinanceTarget

class TargetsViewModel(application: Application) : AndroidViewModel(application) {
    private val _targets = MutableLiveData<List<HomeFinanceTarget>>().apply {
        value = HomeFinanceDatabase.readTargetsData(getApplication())
    }

    val targets : LiveData<List<HomeFinanceTarget>> = _targets

    fun dataChaged(){
        _targets.value = HomeFinanceDatabase.readTargetsData(getApplication())
    }
}