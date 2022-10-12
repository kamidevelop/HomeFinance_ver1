package com.example.homefinance.ui.displayincome

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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.homefinance.*
import com.example.homefinance.usefullextesions.convertLocalIdToDatabaseId
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class HomeFinanceIncomeAdapterData(var context: Context, var dataIncome: List<HomeFinanceIncome>) : RecyclerView.Adapter<HomeFinanceIncomeAdapterData.HomeFinanceIncomeViewHolder>(){
    private var _selectedItem : MutableLiveData<View> = MutableLiveData<View>().apply { value = null }
    val selectedItem : LiveData<View> = _selectedItem
    inner class HomeFinanceIncomeViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var cardView : CardView? = null
        var textNameWhoIncome : TextView? = null
        var textNameIncome : TextView? = null
        var textSumIncome : TextView? = null
        init{
            cardView = itemView.findViewById(R.id.cardOfIncome)
            textNameWhoIncome = itemView.findViewById(R.id.textNameWhoIncome)
            textNameIncome = itemView.findViewById(R.id.textNameIncome)
            textSumIncome = itemView.findViewById(R.id.textSumIncome)
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): HomeFinanceIncomeViewHolder {
        val itemView = LayoutInflater.from(p0.context).inflate(R.layout.homefinance_recyclerview_income_item,p0,false)
        return HomeFinanceIncomeViewHolder(itemView)
    }

    override fun onBindViewHolder(p0: HomeFinanceIncomeViewHolder, p1: Int){
        p0.textNameWhoIncome?.text = HomeFinanceDatabase.readFamilyPersonData(context, dataIncome[p1].nameWho)?.name
        p0.textNameIncome?.text = HomeFinanceDatabase.readFamilyPersonData(context,dataIncome[p1].whose)?.name
        p0.textSumIncome?.text = dataIncome[p1].sum.toString()
        p0.cardView?.setOnClickListener {
            _selectedItem.value = it
        }
    }

    override fun getItemCount(): Int {
        return dataIncome.size
    }

    fun setNewData(newData : List<HomeFinanceIncome>){
        dataIncome = newData
        this.notifyDataSetChanged()
    }
}

class DisplayIncomeFragment : Fragment() {

    companion object {
        fun newInstance() = DisplayIncomeFragment()
    }

    var incomeViewModel: DisplayIncomeViewModel? = null
    private lateinit var recyclerIncome: RecyclerView
    private lateinit var root : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        incomeViewModel = ViewModelProviders.of(this).get(DisplayIncomeViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.display_income_fragment, container, false)
        recyclerIncome = root.findViewById(R.id.recycleOfIncome)
        recyclerIncome.adapter = HomeFinanceIncomeAdapterData(this.context!!,HomeFinanceDatabase.readIncomesDataByDate(this.context!!,SimpleDateFormat("dd-MM-yyyy").format(Date())))
        recyclerIncome.layoutManager = LinearLayoutManager(this.context)
        incomeViewModel?.incomes!!.observe(viewLifecycleOwner,{
            (recyclerIncome.adapter as HomeFinanceIncomeAdapterData).setNewData(it!!)
        })
        (recyclerIncome.adapter as HomeFinanceIncomeAdapterData).selectedItem.observe(viewLifecycleOwner,{
            val selectedCard = it as? CardView
            if(selectedCard != null){
                val intent = Intent(context,MoreIncomeDemandsInformation::class.java)
                intent.putExtra("keyIsIncome",true)
                intent.putExtra("keyClickedCardViewId", HomeFinanceDatabase.convertLocalIdToDatabaseId(
                        root.context,
                        recyclerIncome.getChildAdapterPosition((selectedCard.parent as LinearLayout)),
                        "Incomes"
                ))
                startActivityForResult(intent,1)
            }
        })
        return root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
          //  DisplayIncomeViewModel::class.java
        //)
        // TODO: Use the ViewModel
    }


    fun updateDisplayedData(arguments : Bundle){
        Log.d("[DATE IS]---------------------------",arguments.getString("keyDate",SimpleDateFormat("dd-MM-yyyy").format(Date())))
        //Toast.makeText(this.context!!,arguments.getString("keyDate",DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(Date())),Toast.LENGTH_LONG).show()
        Log.d("[INCOME VIEW MODEL]-----------------",(incomeViewModel == null).toString())
        incomeViewModel?.dataChanged(arguments.getString("keyDate",SimpleDateFormat("dd-MM-yyyy").format(Date())))

    }

}