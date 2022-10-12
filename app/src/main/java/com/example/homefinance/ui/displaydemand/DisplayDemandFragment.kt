package com.example.homefinance.ui.displaydemand

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.homefinance.HomeFinanceDatabase
import com.example.homefinance.HomeFinanceDemand
import com.example.homefinance.MoreIncomeDemandsInformation
import com.example.homefinance.R
import com.example.homefinance.usefullextesions.convertLocalIdToDatabaseId
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class HomeFinanceDemandAdapterData(var context : Context, var dataDemand : List<HomeFinanceDemand>) : RecyclerView.Adapter<HomeFinanceDemandAdapterData.HomeFinanceDemandViewHolder>(){
    private var _selectedItem : MutableLiveData<View> = MutableLiveData<View>().apply { value = null }
    val selectedItem : LiveData<View> = _selectedItem
    inner class HomeFinanceDemandViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var cardView : CardView? = null
        var textNameWhoDemand : TextView? = null
        var textNameDemand : TextView? = null
        var textSumDemand : TextView? = null
        init{
            cardView = itemView.findViewById(R.id.cardOfDemand)
            textNameWhoDemand = itemView.findViewById(R.id.textNameWhoDemand)
            textNameDemand = itemView.findViewById(R.id.textNameDemand)
            textSumDemand = itemView.findViewById(R.id.textSumDemand)
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): HomeFinanceDemandViewHolder {
        val itemView = LayoutInflater.from(p0.context).inflate(R.layout.homefinance_recyclerview_demand_item,p0,false)
        return HomeFinanceDemandViewHolder(itemView)
    }

    override fun onBindViewHolder(p0: HomeFinanceDemandViewHolder, p1: Int) {
        p0.textNameWhoDemand?.text = HomeFinanceDatabase.readFamilyPersonData(context, dataDemand[p1].nameWho)?.name
        p0.textNameDemand?.text = HomeFinanceDatabase.readFamilyPersonData(context, dataDemand[p1].whose)?.name
        p0.textSumDemand?.text = dataDemand[p1].sum.toString()
        p0.cardView?.setOnClickListener {
            _selectedItem.value = it
        }
    }

    override fun getItemCount(): Int {
        return dataDemand.size
    }

    fun setNewData(newData : List<HomeFinanceDemand>){
        dataDemand = newData
        this.notifyDataSetChanged()
    }
}

class DisplayDemandFragment : Fragment() {

    companion object {
        fun newInstance() = DisplayDemandFragment()
    }

    var demandViewModel: DisplayDemandViewModel? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var root : View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.display_demand_fragment, container, false)
        demandViewModel = ViewModelProviders.of(this).get(DisplayDemandViewModel::class.java)
        demandViewModel?.demands!!.observe(viewLifecycleOwner,{
            (recyclerView.adapter as HomeFinanceDemandAdapterData).setNewData(it!!)
        })
        recyclerView = root.findViewById(R.id.recycleOfDemand)
        recyclerView.adapter = HomeFinanceDemandAdapterData(root.context!!,HomeFinanceDatabase.readDemandsDataByDate(root.context!!,SimpleDateFormat("dd-MM-yyyy").format(Date())))
        recyclerView.layoutManager = LinearLayoutManager(root.context!!)
        (recyclerView.adapter as HomeFinanceDemandAdapterData).selectedItem.observe(viewLifecycleOwner,{
            val selectedCard = it as? CardView
            if(selectedCard != null){
                val intent = Intent(context, MoreIncomeDemandsInformation::class.java)
                intent.putExtra("keyIsIncome",false)
                intent.putExtra("keyClickedCardViewId", HomeFinanceDatabase.convertLocalIdToDatabaseId(
                    root.context,
                    recyclerView.getChildAdapterPosition((selectedCard.parent as LinearLayout)),
                    "Demands"
                ))
                startActivityForResult(intent,1)
            }
        })
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
    }

    fun updateDisplayedData(arguments : Bundle){
        demandViewModel?.dataChanged(arguments.getString("keyDate", SimpleDateFormat("dd-MM-yyyy").format(Date())))
    }

}