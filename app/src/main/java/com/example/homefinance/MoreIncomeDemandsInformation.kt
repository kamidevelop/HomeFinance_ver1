package com.example.homefinance

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import org.w3c.dom.Text

class MoreIncomeDemandsInformation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_income_demands_information)
        if(intent.getBooleanExtra("keyIsIncome",true)){
            val income = HomeFinanceDatabase.readIncomeData(this,intent.getIntExtra("keyClickedCardViewId",-1))
            findViewById<TextView>(R.id.dateText).text = income?.dateOfIncome!!
            findViewById<TextView>(R.id.operationText).text = resources.getStringArray(R.array.items_operations)[0]
            findViewById<TextView>(R.id.whoText).text = HomeFinanceDatabase.readFamilyPersonData(this,income?.nameWho!!)?.name
            findViewById<TextView>(R.id.sumText).text = income?.sum!!.toString()
            findViewById<TextView>(R.id.inOrFromText).text = income?.container!!
            findViewById<TextView>(R.id.whoseOrForText).text = HomeFinanceDatabase.readFamilyPersonData(this,income?.whose!!)?.name
            findViewById<TextView>(R.id.categoryOrSourceText).text = income?.source!!
            findViewById<TextView>(R.id.commentText).text = income?.comment
        }
        else{
            val demand = HomeFinanceDatabase.readDemandData(this,intent.getIntExtra("keyClickedCardViewId",-1))
            findViewById<TextView>(R.id.dateText).text = demand?.dateOfDemand!!
            findViewById<TextView>(R.id.operationText).text = resources.getStringArray(R.array.items_operations)[1]
            findViewById<TextView>(R.id.whoText).text = HomeFinanceDatabase.readFamilyPersonData(this,demand?.nameWho!!)?.name
            findViewById<TextView>(R.id.sumText).text = demand?.sum!!.toString()
            findViewById<TextView>(R.id.inOrFromLabel).text = "Из:"
            findViewById<TextView>(R.id.inOrFromText).text = demand?.container!!
            findViewById<TextView>(R.id.whoseOrForLabel).text = "Кого:"
            findViewById<TextView>(R.id.whoseOrForText).text = HomeFinanceDatabase.readFamilyPersonData(this,demand?.whose!!)?.name
            findViewById<TextView>(R.id.categoryOrSourceLabel).text = "Категория:"
            findViewById<TextView>(R.id.categoryOrSourceText).text = demand?.categoryOfDemand!!
            findViewById<TextView>(R.id.commentText).text = demand?.comment
        }
        findViewById<Button>(R.id.cancelInfoButton).setOnClickListener{
            finish()
        }
        findViewById<Button>(R.id.deleteInfoButton).setOnClickListener {
            val id = intent.getIntExtra("keyClickedCardViewId",-1)
            deleteIncomeDemandRecord(id,true)
        }
    }

    fun deleteIncomeDemandRecord(id : Int, isUserDelete : Boolean){
        if(intent.getBooleanExtra("keyIsIncome",true)){
            val income = HomeFinanceDatabase.readIncomeData(this,id)
            when(income?.container) {
                resources.getStringArray(R.array.items_containers)[0]->{
                    val person = HomeFinanceDatabase.readFamilyPersonData(this,income?.whose!!)
                    if(person?.wallet!! >= income?.sum!!) {
                        person?.wallet = person?.wallet?.minus(income?.sum!!)!!
                        Toast.makeText(this,"Запись о доходе удалена, средства списаны из кошелька.",
                            Toast.LENGTH_LONG)
                    }
                    else Toast.makeText(this,"Запись о доходе удалена, недостаточно средств в кошельке для списания.",
                        Toast.LENGTH_LONG)
                    HomeFinanceDatabase.updateFamilyData(this,income?.whose!!,person)
                }
                resources.getStringArray(R.array.items_containers)[1]->{
                    val person = HomeFinanceDatabase.readFamilyPersonData(this,income?.whose!!)
                    if(person?.coinBox!! >= income?.sum!!) {
                        person?.coinBox = person?.wallet?.minus(income?.sum!!)!!
                        Toast.makeText(this,"Запись о доходе удалена, средства списаны из копилки.",
                            Toast.LENGTH_LONG)
                    }
                    else Toast.makeText(this,"Запись о доходе удалена, недостаточно средств в копилке для списания.",
                        Toast.LENGTH_LONG)
                    HomeFinanceDatabase.updateFamilyData(this,income?.whose!!,person)
                }
                resources.getStringArray(R.array.items_containers)[2]->{
                    val balance = HomeFinanceDatabase.readBalance(this)
                    if(balance >= income?.sum!!){
                        HomeFinanceDatabase.writeBalance(this,balance - income?.sum!!)
                        Toast.makeText(this,"Запись о доходе удалена, средства списаны из бюджета.",
                            Toast.LENGTH_LONG)
                    }
                    else Toast.makeText(this,"Запись о доходе удалена, недостаточно средств в бюджете для списания.",
                        Toast.LENGTH_LONG)
                }
            }
            HomeFinanceDatabase.deleteIncomeData(this, id)
        }
        else{
            val demand = HomeFinanceDatabase.readDemandData(this,id)
            when(demand?.container) {
                resources.getStringArray(R.array.items_containers)[0]->{
                    val person = HomeFinanceDatabase.readFamilyPersonData(this,demand?.whose!!)
                    person?.wallet = person?.wallet?.plus(demand?.sum!!)!!
                    if(isUserDelete) Toast.makeText(this,"Запись о расходе удалена, средства возращены в кошелёк.",
                        Toast.LENGTH_LONG)
                    HomeFinanceDatabase.updateFamilyData(this,demand?.whose!!,person)
                }
                resources.getStringArray(R.array.items_containers)[1]->{
                    val person = HomeFinanceDatabase.readFamilyPersonData(this,demand?.whose!!)
                    person?.coinBox = person?.wallet?.plus(demand?.sum!!)!!
                    if(isUserDelete) Toast.makeText(this,"Запись о расходе удалена, средства возвращены в копилку.",
                        Toast.LENGTH_LONG)
                    HomeFinanceDatabase.updateFamilyData(this,demand?.whose!!,person)
                }
                resources.getStringArray(R.array.items_containers)[2]->{
                    val balance = HomeFinanceDatabase.readBalance(this)
                    HomeFinanceDatabase.writeBalance(this,balance + demand?.sum!!)
                    if(isUserDelete) Toast.makeText(this,"Запись о расходе удалена, средства возвращены в бюджет.",
                        Toast.LENGTH_LONG)
                }
            }
            HomeFinanceDatabase.deleteDemandData(this, id)
        }
    }
}