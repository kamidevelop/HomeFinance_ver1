package com.example.homefinance

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.homefinance.usefullextesions.convertDatabaseIdToLocalId
import com.example.homefinance.usefullextesions.convertLocalIdToDatabaseId

class HomeFinanceSpinnerAdapter(context: Context, textViewResId : Int, val names : Array<String>) : ArrayAdapter<String>(context, textViewResId, names) {
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getHomeFinanceView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getHomeFinanceView(position, convertView, parent)
    }

    fun getHomeFinanceView(position : Int, convertView : View?, parent : ViewGroup) : View{
        val item : View = LayoutInflater.from(parent.context!!).inflate(R.layout.homefinance_spinner_item,parent,false)
        val textName = item.findViewById<TextView>(R.id.textNameItem)
        textName.text = names[position]
        return item
    }
}

class AddIncomeDemands : AppCompatActivity() {
    var isEditing : Boolean = false
    val listOfFragments : List<Fragment> = listOf(Income.newInstance("",""),Demands.newInstance("",""))
    private var spinnerForOrWhose : Spinner? = null
    private var textViewForOrWhose : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_income_demands)
        title = intent?.getStringExtra("keyTitleDate")
        val spinnerOfNames = findViewById<Spinner>(R.id.spinnerOfWho)
        val spinnerOperation = findViewById<Spinner>(R.id.spinnerOfTypeOperation)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragmentOfOperations,listOfFragments[0]).commit()
        val saveButton = findViewById<Button>(R.id.saveButtonIncome)
        val cancelButton = findViewById<Button>(R.id.cancelButtonIncome)
        //val deleteButton = findViewById<Button>(R.id.deleteButtonIncome)
        spinnerOfNames.adapter = HomeFinanceSpinnerAdapter(this, R.layout.homefinance_spinner_item,HomeFinanceDatabase.readFamilyData(this).run{
            val listOfNames : MutableList<String> = emptyList<String>().toMutableList()
            for(name in this){
                listOfNames.add(name.name)
            }
            return@run listOfNames.toTypedArray()
        })
        spinnerOperation.adapter = ArrayAdapter.createFromResource(this, R.array.items_operations,R.layout.homefinance_spinner_item)
        spinnerOperation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //Toast.makeText(this@AddIncomeDemands,(view as TextView).text,Toast.LENGTH_LONG).show()
                if((view as TextView).text == resources.getString(R.string.text_demand)){
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragmentOfOperations,listOfFragments[1]).commit()
                }
                else{
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragmentOfOperations,listOfFragments[0]).commit()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
        spinnerOperation.setSelection(0)
        cancelButton.setOnClickListener {
            finish()
        }

        //deleteButton.visibility = View.INVISIBLE
        //isEditing = intent.getBooleanExtra("keyIsEditing",false)

        saveButton.setOnClickListener {
            val operationType =
                findViewById<Spinner>(R.id.spinnerOfTypeOperation).selectedItem.toString()
            if(HomeFinanceDatabase.getFamilyDataCountRecords(this) == 0) {
                Toast.makeText(this, "Ошибка, список членов семьи пуст.", Toast.LENGTH_LONG).show()
                finish()
            }
            val nameWho = findViewById<Spinner>(R.id.spinnerOfWho).selectedItemPosition
            val comment = findViewById<EditText>(R.id.editTextComment).text.toString()
            val sum = if (findViewById<EditText>(R.id.editTextSum).text.toString().isEmpty()) 0.0 else findViewById<EditText>(R.id.editTextSum).text.toString().toDouble()
            if (operationType == resources.getStringArray(R.array.items_operations)[0]) {
                val spinnerIn = findViewById<Spinner>(R.id.spinnerIn).selectedItem.toString()
                val sourceOrIncome = findViewById<EditText>(R.id.editTextSource).text.toString()
                val spinnerFor = if (spinnerIn != resources.getStringArray(R.array.items_containers)[2]) findViewById<Spinner>(R.id.spinnerFor).selectedItemPosition
                    else -1
                HomeFinanceDatabase.writeIncomesData(this, HomeFinanceIncome(HomeFinanceDatabase.convertLocalIdToDatabaseId(this, nameWho, "Family"), title.toString(),
                        sum,
                        spinnerIn,
                        HomeFinanceDatabase.convertLocalIdToDatabaseId(this, spinnerFor,"Family"),
                        comment,
                        sourceOrIncome),
                    HomeFinanceDatabase.convertLocalIdToDatabaseId(
                        this,
                        findViewById<Spinner>(R.id.spinnerFor).selectedItemPosition,
                        "Family"))
            } else {
                val frLay = findViewById<FrameLayout>(R.id.fragmentOfOperations)
                val spinnerFrom = frLay.findViewById<Spinner>(R.id.spinnerFrom).selectedItem.toString()
                val spinnerCategory =
                    frLay.findViewById<Spinner>(R.id.spinnerOfCategory)
                var spinnerWhose = if(spinnerFrom != resources.getStringArray(R.array.items_containers)[2]) frLay.findViewById<Spinner>(R.id.spinnerWhose).selectedItemPosition else -1
                spinnerWhose = HomeFinanceDatabase.convertLocalIdToDatabaseId(this,spinnerWhose,"Family")
                HomeFinanceDatabase.writeDemandsData(this,
                    HomeFinanceDemand(HomeFinanceDatabase.convertLocalIdToDatabaseId(this,nameWho,"Family"),title.toString(),sum,spinnerFrom, spinnerWhose,comment,if(spinnerCategory.childCount > 0)spinnerCategory.selectedItem.toString() else ""),
                    spinnerWhose
                )
            }
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
    
}