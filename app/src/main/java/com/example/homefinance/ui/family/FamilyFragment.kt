package com.example.homefinance.ui.family


import android.app.Activity
import android.app.ApplicationErrorReport
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.support.v4.app.Fragment
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.example.homefinance.AddInFamilyActivity
import com.example.homefinance.HomeFinanceDatabase
import com.example.homefinance.HomeFinancePerson
import com.example.homefinance.R
import com.example.homefinance.usefullextesions.*
import org.w3c.dom.Text
import java.text.FieldPosition


class HomeFinancePersonAdaptedData(var dataPersons : List<HomeFinancePerson>, val context: Context) : RecyclerView.Adapter<HomeFinancePersonAdaptedData.HomeFinanceViewHolder>(){
    private var _selectedItem : MutableLiveData<View> = MutableLiveData<View>().apply { value = null }
    val selectedItem : LiveData<View> = _selectedItem

    inner class HomeFinanceViewHolder(val itemView : View) : RecyclerView.ViewHolder(itemView){
        var personImage : ImageView? = null
        var personName : TextView? = null
        var personSourceOfIncome : TextView? = null
        var personCoinBox : TextView? = null
        var cardView : CardView? = null
        init {
            personImage = itemView.findViewById(R.id.imagePhotoPerson)
            personName = itemView.findViewById(R.id.textName)
            personSourceOfIncome = itemView.findViewById(R.id.textSourceOfIncome)
            personCoinBox = itemView.findViewById(R.id.textCoinBox)
            cardView = itemView.findViewById(R.id.personCard)
        }

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): HomeFinanceViewHolder {
        val itemView = LayoutInflater.from(p0.context).inflate(R.layout.homefinance_recyclerview_item,p0,false)
        return HomeFinanceViewHolder(itemView)
    }

    override fun onBindViewHolder(p0: HomeFinanceViewHolder, position: Int) {
        if(dataPersons[position].photo == "no_photo") p0.personImage?.setImageResource(R.drawable.ic_nophoto_256dp)
        else p0.personImage?.setImageURI(Uri.parse(dataPersons[position].photo))
        p0.personName?.text = dataPersons[position].name
        p0.personSourceOfIncome?.text = dataPersons[position].sourceOfIncome
        p0.personCoinBox?.text = dataPersons[position].coinBox.toString()
        p0.cardView?.setOnClickListener {
            _selectedItem.value = it
        }
    }

    fun setNewData(newDatas : List<HomeFinancePerson>){
        dataPersons = newDatas
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return dataPersons.size
    }
}

class FamilyFragment : Fragment(){

    private lateinit var familyViewModel: FamilyViewModel
    private var selectedView: CardView? = null
    private lateinit var root : View
    private lateinit var recyclerView: RecyclerView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        familyViewModel = ViewModelProviders.of(this).get(FamilyViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_family, container, false)
        val floatingButton = root.findViewById<FloatingActionButton>(R.id.addInFamilyFloatingButton)
        recyclerView = root.findViewById(R.id.recyclerPersons)
        floatingButton.setOnClickListener(View.OnClickListener{
            val intentAddInFamilyActivity = Intent(root.context,AddInFamilyActivity::class.java)
            intentAddInFamilyActivity.putExtra("keyIsEditing",false)
            startActivityForResult(intentAddInFamilyActivity,0)
        })
        recyclerView.adapter = HomeFinancePersonAdaptedData(HomeFinanceDatabase.readFamilyData(root.context),this.context!!)
        familyViewModel.infoFamily.observe(viewLifecycleOwner, Observer {
            (recyclerView.adapter as HomeFinancePersonAdaptedData).setNewData(it!!)
        })
        (recyclerView.adapter as HomeFinancePersonAdaptedData).selectedItem.observe(viewLifecycleOwner, Observer {
            selectedView = it as? CardView
            if(selectedView != null) {
                val intent = Intent(root.context, AddInFamilyActivity::class.java)
                intent.putExtra("keyIsEditing", true)
                intent.putExtra(
                    "keyClickedCardViewId",
                    HomeFinanceDatabase.convertLocalIdToDatabaseId(
                        root.context,
                        recyclerView.getChildAdapterPosition((selectedView?.parent as LinearLayout)),
                        "Family"
                    )
                )
                startActivityForResult(intent, 1)
            }
        })
        recyclerView.layoutManager = LinearLayoutManager(root.context)
        //Берём position в качестве local Id
        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            familyViewModel.dataChanged()
        }
    }

    fun updateCardData(cardView: CardView, newDataPerson: HomeFinancePerson){
        val listTextView = cardView.getChildsOfType<TextView>()
        val listImage = cardView.getChildsOfType<ImageView>()
        if(!listTextView.isEmpty()){
            listTextView[0].text = newDataPerson.name
            listTextView[1].text = newDataPerson.sourceOfIncome
            listTextView[2].text = newDataPerson.coinBox.toString()
        }
        if(!listImage.isEmpty()) {
            if(newDataPerson.photo == "no_photo") listImage[0].setImageResource(R.drawable.ic_nophoto_256dp)
            else listImage[0].setImageURI(Uri.parse(newDataPerson.photo))
        }
    }
}