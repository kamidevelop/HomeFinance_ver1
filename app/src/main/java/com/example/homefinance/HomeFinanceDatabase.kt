package com.example.homefinance

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.widget.DatePicker
import android.widget.Toast
import com.example.homefinance.usefullextesions.convertLocalIdToDatabaseId
import kotlin.collections.*


data class HomeFinancePerson(val name : String, val categoriesOfDemands : String, val sourceOfIncome : String, var coinBox : Double, var wallet : Double, val photo : String)
data class HomeFinanceIncome(val nameWho : Int, val dateOfIncome : String, val sum : Double, val container : String, val whose : Int, val comment : String, val source : String)
data class HomeFinanceDemand(val nameWho : Int, val dateOfDemand : String, val sum : Double, val container : String, val whose : Int, val comment : String, val categoryOfDemand: String)
data class HomeFinanceTarget(val name : String, val endSum : Double, val currentSum : Double, val photo : String)

class HomeFinanceDatabase{
    companion object {
        const val DATA_BASE_NAME = "homefinance.db"
        fun readFamilyData(context: Context): List<HomeFinancePerson> {
            val listOfPersons: MutableList<HomeFinancePerson> = mutableListOf()
            val database : SQLiteDatabase = context.openOrCreateDatabase(DATA_BASE_NAME,Context.MODE_PRIVATE,null)
            if (database.isOpen) {
                val curs = database.rawQuery("SELECT * FROM Family;", null)
                if(curs.moveToFirst()) {
                    listOfPersons.add(
                        HomeFinancePerson(
                            curs.getString(1),
                            curs.getString(2),
                            curs.getString(3),
                            curs.getDouble(4),
                                curs.getDouble(5),
                                curs.getString(6)
                        )
                    )
                }
                while (curs.moveToNext()) {
                    listOfPersons.add(
                        HomeFinancePerson(
                            curs.getString(1),
                            curs.getString(2),
                            curs.getString(3),
                            curs.getDouble(4),
                                curs.getDouble(5),
                                curs.getString(6)
                        )
                    )
                }
                curs.close()
                database.close()
            }
            return listOfPersons.toList()
        }

        fun readFamilyPersonData(context: Context, personId: Int) : HomeFinancePerson?{
            val database : SQLiteDatabase = context.openOrCreateDatabase(DATA_BASE_NAME,Context.MODE_PRIVATE,null)
            if(database.isOpen) {
                val curs = database.rawQuery("SELECT * FROM Family WHERE id = $personId;",null)
                if(curs.moveToFirst()){
                    val person = HomeFinancePerson(curs.getString(1),curs.getString(2),curs.getString(3),curs.getDouble(4), curs.getDouble(5),curs.getString(6))
                    curs.close()
                    database.close()
                    return person
                }
                else database.close()
            }
            return null
        }

        fun writeFamilyData(context: Context, person: HomeFinancePerson){
            val database : SQLiteDatabase = context.openOrCreateDatabase(DATA_BASE_NAME,Context.MODE_PRIVATE,null)
            if(database.isOpen){
                val contentValues = ContentValues()
                contentValues.put("name", person.name)
                contentValues.put("categoriesOfDemands",person.categoriesOfDemands)
                contentValues.put("sourceOfIncome",person.sourceOfIncome)
                contentValues.put("coinBoxMoney",person.coinBox)
                contentValues.put("wallet",person.wallet)
                contentValues.put("photo",person.photo)
                database.insert("Family",null,contentValues)
                database.close()
            }
        }

        fun updateFamilyData(context: Context, personId : Int, person: HomeFinancePerson){
            val database : SQLiteDatabase = context.openOrCreateDatabase(DATA_BASE_NAME,Context.MODE_PRIVATE,null)
            if(database.isOpen){
                val contentValues = ContentValues()
                contentValues.put("name", person.name)
                contentValues.put("categoriesOfDemands",person.categoriesOfDemands)
                contentValues.put("sourceOfIncome",person.sourceOfIncome)
                contentValues.put("coinBoxMoney",person.coinBox)
                contentValues.put("photo",person.photo)
                database.update("Family", contentValues,"id = ?", arrayOf(personId.toString()))
                database.close()
            }
        }

        fun deleteFamilyData(context: Context, personId : Int) : Int{
            if(personId == -1) return -1
            var database : SQLiteDatabase = context.openOrCreateDatabase(DATA_BASE_NAME, Context.MODE_PRIVATE, null)
            if(database.isOpen){
                database.delete("Family","id = ?", arrayOf(personId.toString()))
                database.close()
            }
            return personId
        }

        fun getFamilyDataCountRecords(context : Context): Int {
            val database : SQLiteDatabase = context.openOrCreateDatabase(DATA_BASE_NAME,Context.MODE_PRIVATE,null)
            val count = database.rawQuery("SELECT * FROM Family;", null).count
            database.close()
            return count
        }

        fun readIncomesData(context: Context) : List<HomeFinanceIncome>{
            val listOfIncomes = mutableListOf<HomeFinanceIncome>()
            val database = context.openOrCreateDatabase(DATA_BASE_NAME,Context.MODE_PRIVATE,null)
            if(database.isOpen){
                val curs = database.rawQuery("SELECT * FROM Incomes;", null)
                if(curs.moveToFirst()) {
                    listOfIncomes.add(
                        HomeFinanceIncome(
                            curs.getInt(1),
                            curs.getString(2),
                            curs.getDouble(6),
                            curs.getString(4),
                            curs.getInt(5),
                            curs.getString(7),
                            curs.getString(3))
                    )
                }
                while (curs.moveToNext()) {
                    listOfIncomes.add(
                        HomeFinanceIncome(
                            curs.getInt(1),
                            curs.getString(2),
                            curs.getDouble(6),
                            curs.getString(4),
                            curs.getInt(5),
                            curs.getString(7),
                            curs.getString(3))
                    )
                }
                curs.close()
                database.close()
            }
            return listOfIncomes
        }

        fun readIncomeData(context: Context, personId: Int) : HomeFinanceIncome?{
            val database = context.openOrCreateDatabase(DATA_BASE_NAME,Context.MODE_PRIVATE,null)
            if(database.isOpen){
                val curs = database.rawQuery("SELECT * FROM Incomes WHERE id = "+personId+";",null)
                if(curs.moveToFirst()){
                    val income = HomeFinanceIncome(curs.getInt(1),curs.getString(2),curs.getDouble(6),curs.getString(4),curs.getInt(5),curs.getString(7),curs.getString(3))
                    curs.close()
                    database.close()
                    return income
                }
                database.close()
            }
            return null
        }

        fun readIncomesDataByDate(context: Context, date : String) : List<HomeFinanceIncome>{
            val listOfIncomes = mutableListOf<HomeFinanceIncome>()
            val database = context.openOrCreateDatabase(DATA_BASE_NAME,Context.MODE_PRIVATE,null)
            if(database.isOpen) {
                val curs = database.rawQuery("SELECT * FROM Incomes WHERE dateOfIncome = '$date';", null)
                if (curs.moveToFirst()) {
                    listOfIncomes.add(
                            HomeFinanceIncome(
                                    curs.getInt(1),
                                    curs.getString(2),
                                    curs.getDouble(6),
                                    curs.getString(4),
                                    curs.getInt(5),
                                    curs.getString(7),
                                    curs.getString(3))
                    )
                }
                while (curs.moveToNext()) {
                    listOfIncomes.add(
                            HomeFinanceIncome(
                                    curs.getInt(1),
                                    curs.getString(2),
                                    curs.getDouble(6),
                                    curs.getString(4),
                                    curs.getInt(5),
                                    curs.getString(7),
                                    curs.getString(3))
                    )
                }
                curs.close()
                database.close()
            }
            return listOfIncomes
        }

        fun writeIncomesData(context: Context, income : HomeFinanceIncome, whoseId : Int){
            var database = context.openOrCreateDatabase(DATA_BASE_NAME,Context.MODE_PRIVATE,null)
            if(database.isOpen){
                var contentValues = ContentValues()
                when(income.container){
                    context.resources.getStringArray(R.array.items_containers)[0]->{
                        val curs = database.rawQuery("SELECT wallet FROM Family WHERE id = "+whoseId+";",null)
                        if(curs.moveToFirst()){
                            val wallet = curs.getDouble(0)
                            contentValues = ContentValues()
                            contentValues.put("wallet",wallet+income.sum)
                            database.update("Family",contentValues,"id = "+whoseId,null)
                        }
                        else{
                            
                        }
                        contentValues = ContentValues()
                        contentValues.put("name",income.nameWho)
                        contentValues.put("dateOfIncome",income.dateOfIncome)
                        contentValues.put("sourceOfIncome",income.source)
                        contentValues.put("container",income.container)
                        contentValues.put("whose",income.whose)
                        contentValues.put("sum",income.sum)
                        contentValues.put("comment",income.comment)
                        database.insert("Incomes",null,contentValues)
                        database.close()
                    }
                    context.resources.getStringArray(R.array.items_containers)[1]->{
                        val curs = database.rawQuery("SELECT coinBoxMoney FROM Family WHERE id = "+whoseId+";",null)
                        if(curs.moveToFirst()){
                            val coinBox = curs.getDouble(0)
                            contentValues = ContentValues()
                            contentValues.put("coinBoxMoney",coinBox+income.sum)
                            database.update("Family",contentValues,"id = "+whoseId,null)
                            contentValues = ContentValues()
                            contentValues.put("name",income.nameWho)
                            contentValues.put("dateOfIncome",income.dateOfIncome)
                            contentValues.put("sourceOfIncome",income.source)
                            contentValues.put("container",income.container)
                            contentValues.put("whose",income.whose)
                            contentValues.put("sum",income.sum)
                            contentValues.put("comment",income.comment)
                            database.insert("Incomes",null,contentValues)
                        }
                    }
                    context.resources.getStringArray(R.array.items_containers)[2]->{
                        val curs = database.rawQuery("SELECT balanceBudget FROM Balance;",null)
                        if(curs.moveToFirst()){
                            val budget = curs.getDouble(0)
                            contentValues = ContentValues()
                            contentValues.put("balanceBudget",budget+income.sum)
                            database.update("Balance",contentValues,"id = 1",null)
                            contentValues = ContentValues()
                            contentValues.put("name",income.nameWho)
                            contentValues.put("dateOfIncome",income.dateOfIncome)
                            contentValues.put("sourceOfIncome",income.source)
                            contentValues.put("container",income.container)
                            contentValues.put("whose",income.whose)
                            contentValues.put("sum",income.sum)
                            contentValues.put("comment",income.comment)
                            database.insert("Incomes",null,contentValues)
                        }
                    }
                }
            }
            database.close()
        }

        fun deleteIncomeData(context: Context, incomeId : Int){
            if(incomeId == -1) return
            val database = context.openOrCreateDatabase(DATA_BASE_NAME,Context.MODE_PRIVATE,null)
            if(database.isOpen){
                database.delete("Incomes","id = $incomeId",null)
                database.close()
            }
        }

        fun readDemandsData(context: Context) : List<HomeFinanceDemand>{
            val listOfDemands = mutableListOf<HomeFinanceDemand>()
            val database = context.openOrCreateDatabase(DATA_BASE_NAME,Context.MODE_PRIVATE,null)
            if(database.isOpen){
                val curs = database.rawQuery("SELECT * FROM Demands;", null)
                if(curs.moveToFirst()) {
                    listOfDemands.add(
                        HomeFinanceDemand(
                            curs.getInt(1),
                            curs.getString(2),
                            curs.getDouble(6),
                            curs.getString(4),
                            curs.getInt(5),
                            curs.getString(7),
                            curs.getString(3))
                    )
                }
                while (curs.moveToNext()) {
                    listOfDemands.add(
                        HomeFinanceDemand(
                            curs.getInt(1),
                            curs.getString(2),
                            curs.getDouble(6),
                            curs.getString(4),
                            curs.getInt(5),
                            curs.getString(7),
                            curs.getString(3))
                    )
                }
            }
            database.close()
            return listOfDemands.toList()
        }

        fun readDemandsDataByDate(context: Context, date : String) : List<HomeFinanceDemand> {
            val listOfDemands = mutableListOf<HomeFinanceDemand>()
            val database = context.openOrCreateDatabase(DATA_BASE_NAME,Context.MODE_PRIVATE,null)
            if(database.isOpen){
                val curs = database.rawQuery("SELECT * FROM Demands WHERE dateOfDemand = '$date';", null)
                if(curs.moveToFirst()) {
                    listOfDemands.add(
                            HomeFinanceDemand(
                                    curs.getInt(1),
                                    curs.getString(2),
                                    curs.getDouble(6),
                                    curs.getString(4),
                                    curs.getInt(5),
                                    curs.getString(7),
                                    curs.getString(3))
                    )
                }
                while (curs.moveToNext()) {
                    listOfDemands.add(
                            HomeFinanceDemand(
                                    curs.getInt(1),
                                    curs.getString(2),
                                    curs.getDouble(6),
                                    curs.getString(4),
                                    curs.getInt(5),
                                    curs.getString(7),
                                    curs.getString(3))
                    )
                }
            }
            database.close()
            return listOfDemands.toList()
        }

        fun writeDemandsData(context: Context, demand: HomeFinanceDemand, whoseId: Int){
            var database = context.openOrCreateDatabase(DATA_BASE_NAME,Context.MODE_PRIVATE,null)
            if(database.isOpen){
                var contentValues = ContentValues()
                when(demand.container){
                    context.resources.getStringArray(R.array.items_containers)[0]->{
                        val curs = database.rawQuery("SELECT wallet FROM Family WHERE id = "+whoseId+";",null)
                        if(curs.moveToFirst()){
                            val wallet = curs.getDouble(0)
                            contentValues = ContentValues()
                            if(wallet >= demand.sum)
                                contentValues.put("wallet",wallet-demand.sum)
                            else{
                                database.close()
                                Toast.makeText(context,context.resources.getStringArray(R.array.errors_messages)[0],Toast.LENGTH_LONG).show()
                                return
                            }
                            database.update("Family",contentValues,"id = "+whoseId,null)
                        }
                        else {
                            Toast.makeText(context, "Члены семьи не заданы.",Toast.LENGTH_LONG).show()
                            return
                        }
                        contentValues = ContentValues()
                        contentValues.put("name", demand.nameWho)
                        contentValues.put("dateOfDemand",demand.dateOfDemand)
                        contentValues.put("categoryOfDemands",demand.categoryOfDemand)
                        contentValues.put("container",demand.container)
                        contentValues.put("whose",demand.whose)
                        contentValues.put("sum",demand.sum)
                        contentValues.put("comment",demand.comment)
                        database.insert("Demands",null,contentValues)
                        database.close()
                    }
                    context.resources.getStringArray(R.array.items_containers)[1]->{
                        val curs = database.rawQuery("SELECT coinBoxMoney FROM Family WHERE id = "+whoseId+";",null)
                        if(curs.moveToFirst()){
                            val coinBox = curs.getDouble(0)
                            contentValues = ContentValues()
                            if(coinBox >= demand.sum)
                                contentValues.put("coinBoxMoney",coinBox-demand.sum)
                            else{
                                database.close()
                                Toast.makeText(context,context.resources.getStringArray(R.array.errors_messages)[2],Toast.LENGTH_LONG).show()
                                return
                            }
                            database.update("Family",contentValues,"id = "+whoseId,null)
                        }
                        else{
                            Toast.makeText(context, "Члены семьи не заданы.",Toast.LENGTH_LONG).show()
                            return
                        }
                        contentValues = ContentValues()
                        contentValues.put("name",demand.nameWho)
                        contentValues.put("dateOfDemand",demand.dateOfDemand)
                        contentValues.put("categoryOfDemands", demand.categoryOfDemand)
                        contentValues.put("container",demand.container)
                        contentValues.put("whose",demand.whose)
                        contentValues.put("sum",demand.sum)
                        contentValues.put("comment",demand.comment)
                        database.insert("Demands",null,contentValues)
                    }
                    context.resources.getStringArray(R.array.items_containers)[2]->{
                        val curs = database.rawQuery("SELECT balanceBudget FROM Balance;",null)
                        if(curs.moveToFirst()){
                            val budget = curs.getDouble(0)
                            contentValues = ContentValues()
                            if(budget >= demand.sum)
                                contentValues.put("balanceBudget",budget-demand.sum)
                            else{
                                database.close()
                                Toast.makeText(context,context.resources.getStringArray(R.array.errors_messages)[1],Toast.LENGTH_LONG).show()
                                return
                            }
                            database.update("Balance",contentValues,"id = 1",null)
                            contentValues = ContentValues()
                            contentValues.put("name",demand.nameWho)
                            contentValues.put("dateOfDemand",demand.dateOfDemand)
                            contentValues.put("categoryOfDemands",demand.categoryOfDemand)
                            contentValues.put("container",demand.container)
                            contentValues.put("whose","")
                            contentValues.put("sum",demand.sum)
                            contentValues.put("comment",demand.comment)
                            database.insert("Demands",null,contentValues)
                        }
                    }
                }
            }
            database.close()
        }

        fun readDemandData(context: Context, demandId: Int) : HomeFinanceDemand?{
            val database = context.openOrCreateDatabase(DATA_BASE_NAME,Context.MODE_PRIVATE,null)
            if(database.isOpen) {
                val curs = database.rawQuery("SELECT * FROM Demands WHERE id = $demandId;", null)
                if (curs.moveToFirst()) {
                    val demand = HomeFinanceDemand(curs.getInt(1), curs.getString(2), curs.getDouble(6),
                            curs.getString(4),
                            curs.getInt(5),
                            curs.getString(7),
                            curs.getString(3))
                    curs.close()
                    database.close()
                    return demand
                }
                curs.close()
                database.close()
            }
            return null
        }

        fun deleteDemandData(context: Context, demandId : Int){
            if(demandId == -1)return
            val database = context.openOrCreateDatabase(DATA_BASE_NAME,Context.MODE_PRIVATE,null)
            if(database.isOpen){
                database.delete("Demands","id = $demandId",null)
                database.close()
            }
        }

        fun writeTargetData(context: Context, target : HomeFinanceTarget){
            val database = context.openOrCreateDatabase(DATA_BASE_NAME,Context.MODE_PRIVATE,null)
            if(database.isOpen){
                val contentValues = ContentValues()
                contentValues.put("name",target.name)
                contentValues.put("endSum",target.endSum)
                contentValues.put("currentSum",target.currentSum)
                contentValues.put("photoPath",target.photo)
                database.insert("Targets",null,contentValues)
                database.close()
            }
        }

        fun deleteTargetData(context: Context, targetId: Int){
            if(targetId == -1) return
            val database = context.openOrCreateDatabase(DATA_BASE_NAME,Context.MODE_PRIVATE,null)
            if(database.isOpen){
                database.delete("Targets","id = "+targetId,null)
                database.close()
            }
        }

        fun readTargetsData(context: Context) : List<HomeFinanceTarget>{
            var targetsList : MutableList<HomeFinanceTarget> = mutableListOf()
            val database = context.openOrCreateDatabase(DATA_BASE_NAME,Context.MODE_PRIVATE,null)
            if(database.isOpen){
                val curs = database.rawQuery("SELECT * FROM Targets;",null)
                if(curs.moveToFirst()){
                    targetsList.add(HomeFinanceTarget(
                            curs.getString(1),
                            curs.getDouble(2),
                            curs.getDouble(3),
                            curs.getString(4)))
                }
                while(curs.moveToNext()){
                    targetsList.add(HomeFinanceTarget(
                            curs.getString(1),
                            curs.getDouble(2),
                            curs.getDouble(3),
                            curs.getString(4)))
                }
                curs.close()
                database.close()
            }
            return targetsList
        }

        fun updateTargetSum(context: Context, newCurrentSum : Double, targetId : Int){
            val database = context.openOrCreateDatabase(DATA_BASE_NAME,Context.MODE_PRIVATE,null)
            if(database.isOpen){
                val contentValues = ContentValues()
                contentValues.put("currentSum",newCurrentSum)
                database.update("Targets",contentValues,"id = "+targetId,null)
                database.close()
            }
        }

        fun updateTargetData(context: Context, newTarget : HomeFinanceTarget, targetId: Int){
            val database = context.openOrCreateDatabase(DATA_BASE_NAME,Context.MODE_PRIVATE,null)
            if(database.isOpen){
                val contentValues = ContentValues()
                contentValues.put("name",newTarget.name)
                contentValues.put("endSum",newTarget.endSum)
                contentValues.put("currentSum",newTarget.currentSum)
                contentValues.put("photoPath",newTarget.photo)
                database.update("Targets",contentValues,"id = "+targetId,null)
                database.close()
            }
        }

        fun readTargetData(context: Context, targetId : Int) : HomeFinanceTarget? {
            val database = context.openOrCreateDatabase(DATA_BASE_NAME,Context.MODE_PRIVATE,null)
            var target : HomeFinanceTarget? = null
            if(database.isOpen){
                val curs = database.rawQuery("SELECT * FROM Targets WHERE id = "+targetId+";",null)
                if(curs.moveToFirst()){
                    target = HomeFinanceTarget(curs.getString(1),curs.getDouble(2),curs.getDouble(3),curs.getString(4))
                }
                curs.close()
                database.close()
            }
            return target
        }

        fun readBalance(context : Context) : Double {
            val database = context.openOrCreateDatabase(DATA_BASE_NAME,Context.MODE_PRIVATE,null)
            var balance = -1.0
            if(database.isOpen){
                val curs = database.rawQuery("SELECT balanceBudget FROM Balance;",null)
                if(curs.moveToFirst()){
                    balance = curs.getDouble(0)
                }
                curs.close()
                database.close()
            }
            return balance
        }

        fun writeBalance(context: Context, balance : Double){
            val database = context.openOrCreateDatabase(DATA_BASE_NAME,Context.MODE_PRIVATE,null)
            if(database.isOpen){
                if(database.rawQuery("SELECT * FROM Balance;",null).count <= 0){
                    val contentValues = ContentValues()
                    contentValues.put("balanceBudget",balance)
                    database.insert("Balance",null,contentValues)
                }
                else{
                    val contentValues = ContentValues()
                    contentValues.put("balanceBudget",balance)
                    database.update("Balance",contentValues,"id = 1",null)
                }
                database.close()
            }
        }

        fun initPreparing(context: Context) {
            val database : SQLiteDatabase = context.openOrCreateDatabase(DATA_BASE_NAME,Context.MODE_PRIVATE,null)
            database.execSQL("CREATE TABLE IF NOT EXISTS Family (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, categoriesOfDemands TEXT, sourceOfIncome TEXT, coinBoxMoney REAL, wallet REAL, photo TEXT);")
            database.execSQL("CREATE TABLE IF NOT EXISTS Incomes (id INTEGER PRIMARY KEY AUTOINCREMENT, name INTEGER, dateOfIncome TEXT, sourceOfIncome TEXT, container TEXT, whose INTEGER, sum REAL, comment TEXT);")
            database.execSQL("CREATE TABLE IF NOT EXISTS Demands (id INTEGER PRIMARY KEY AUTOINCREMENT, name INTEGER, dateOfDemand TEXT, categoryOfDemands TEXT, container TEXT, whose INTEGER, sum REAL, comment TEXT);")
            database.execSQL("CREATE TABLE IF NOT EXISTS Targets (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, endSum REAL, currentSum REAL, photoPath TEXT);")
            database.execSQL("CREATE TABLE IF NOT EXISTS Balance (id INTEGER PRIMARY KEY AUTOINCREMENT, balanceBudget REAL);")
            if(database.rawQuery("SELECT * FROM Balance;",null).count <= 0){
                val contentValues = ContentValues()
                contentValues.put("balanceBudget",0.0)
                database.insert("Balance",null,contentValues)
            }
            database.close()
        }
    }
}