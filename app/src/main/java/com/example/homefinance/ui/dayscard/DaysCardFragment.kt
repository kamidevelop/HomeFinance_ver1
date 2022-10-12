package com.example.homefinance.ui.dayscard

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v4.app.Fragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.net.Uri
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.*
import com.example.homefinance.HomeFinanceDatabase
import com.example.homefinance.HomeFinancePerson
import com.example.homefinance.HomeFinanceTarget
import com.example.homefinance.R
import java.text.SimpleDateFormat
import java.util.*

class HomeFinanceCDAdaptedData(var dataTargets : List<HomeFinanceTarget>, val context: Context) : RecyclerView.Adapter<HomeFinanceCDAdaptedData.HomeFinanceCDViewHolder>(){

    inner class HomeFinanceCDViewHolder(val itemView : View) : RecyclerView.ViewHolder(itemView){
        var targetImage : ImageView? = null
        var targetName : TextView? = null
        var targetStatus : TextView? = null
        var targetProgresss : ProgressBar? = null
        //var cardView : CardView? = null
        init {
            targetImage = itemView.findViewById(R.id.imageTargetCD)
            targetName = itemView.findViewById(R.id.textNameTargetCD)
            targetStatus = itemView.findViewById(R.id.textStatusCD)
            targetProgresss = itemView.findViewById(R.id.progressTargetCD)
        }

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): HomeFinanceCDViewHolder {
        val itemView = LayoutInflater.from(p0.context).inflate(R.layout.homefinance_recycleview_getedtargets_item,p0,false)
        return HomeFinanceCDViewHolder(itemView)
    }

    override fun onBindViewHolder(p0: HomeFinanceCDViewHolder, position: Int) {
        if(dataTargets[position].photo == "no_photo") p0.targetImage?.setImageResource(R.drawable.ic_baseline_photo_camera_24)
        else p0.targetImage?.setImageURI(Uri.parse(dataTargets[position].photo))
        p0.targetName?.text = dataTargets[position].name
        p0.targetStatus?.text = if(dataTargets[position].currentSum < dataTargets[position].endSum) context.resources.getString(R.string.text_in_progress) else
            context.resources.getString(R.string.text_complete)
        p0.targetProgresss?.max = dataTargets[position].endSum.toInt()
        p0.targetProgresss?.setProgress(dataTargets[position].currentSum.toInt())
    }


    override fun getItemCount(): Int {
        return dataTargets.size
    }
}


class DaysCardFragment : Fragment() {

    private lateinit var daysCardViewModel: DaysCardViewModel
    private lateinit var recyclerOfGetedTargets : RecyclerView
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        daysCardViewModel =
                ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(DaysCardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_days_card, container, false)
        recyclerOfGetedTargets = root.findViewById(R.id.recyclerViewGetedTargets)
        root.findViewById<EditText>(R.id.editTextBudget).setText(HomeFinanceDatabase.readBalance(this.context!!).toString())
        val buttonSave = root.findViewById<Button>(R.id.buttonSetAndSave)
        buttonSave.setOnClickListener{
            val editTextBudget = (it.parent as View).findViewById<EditText>(R.id.editTextBudget)
            HomeFinanceDatabase.writeBalance(this.context!!,editTextBudget.text.toString().toDouble())
        }

        recyclerOfGetedTargets.adapter = HomeFinanceCDAdaptedData(HomeFinanceDatabase.readTargetsData(this.context!!),this.context!!)
        recyclerOfGetedTargets.layoutManager = LinearLayoutManager(this.context)
        val textViewIncomeCD = root.findViewById<TextView>(R.id.textViewDisplayIncomeCD)
        val textViewDemandCD = root.findViewById<TextView>(R.id.textViewDisplayDemandsCD)
        textViewIncomeCD.text = HomeFinanceDatabase.readIncomesDataByDate(root.context,SimpleDateFormat("dd-MM-yyyy").format(Date())).run {
            var sum = 0.0
            for(income in this){
                sum += income.sum
            }
            return@run sum.toString()
        }
        textViewDemandCD.text = HomeFinanceDatabase.readDemandsDataByDate(root.context,SimpleDateFormat("dd-MM-yyyy").format(Date())).run{
            var sum = 0.0
            for(demand in this){
                sum+=demand.sum
            }
            return@run sum.toString()
        }
        return root
    }
}