package com.example.homefinance.usefullextesions

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.example.homefinance.HomeFinanceDatabase
import com.example.homefinance.HomeFinancePerson

inline fun <reified T> ViewGroup.getChildsOfType() : List<T>{
    var mList : MutableList<T> = emptyList<T>().toMutableList()
    for(id in 0..this.childCount){
        if(this.getChildAt(id) is T)
            mList.add((this.getChildAt(id) as T))
    }
    return mList.toList()
}

fun <T> ViewGroup.getIdOfChild(child : T) : Int where T : View {
    var id = -1
    Log.d("getIdOfChild","-------------------enter--------------------")
    //val parentOfChild = child.parent as ViewGroup
    for(idC in 0..childCount){
        if(child === getChildAt(idC)){
            id = idC
            Log.d("getIdOfChild","----------------------------------------Ok-----------------------------")
            break
        }
    }
    Log.d("getIdOfChild","-------------------------exit---------------------------")
    return id
}
fun HomeFinanceDatabase.Companion.getCategoriesFromCategoryString(person : HomeFinancePerson) : List<String>{
    val list = person?.categoriesOfDemands!!.split(",")
    var newList : MutableList<String> = mutableListOf()
    for(category in list){
        newList.add(category.replace("'",""))
    }
    return newList
}

fun HomeFinanceDatabase.Companion.convertLocalIdToDatabaseId(context : Context, localId : Int, table : String) : Int{
    var id = -1
    if(localId < 0) {
        Log.d("convertLIDtoDBID","----------------------------------------localId < 0--------------------------------------------")
        return -1
    }
    var curcount = -1
    val database : SQLiteDatabase = context.openOrCreateDatabase(HomeFinanceDatabase.DATA_BASE_NAME,Context.MODE_PRIVATE,null)
    if(database.isOpen){
        val curs = database.rawQuery("SELECT id FROM "+table +";",null)
        curcount = curs.count
        //if(localId >= curs.count)return -1
        if(curs.moveToPosition(localId)){
            Log.d("convertLIDtoDBID","---------------------------------------------------OK---------------------------------------")
           id = curs.getInt(0)
        }
        else{
            Log.d("convertLIDtoDBID","----------------------------------error with moveToPosition $localId cursCount $curcount-----------------------------------")
            return -1
        }
    }
    database.close()
    return id
}

fun HomeFinanceDatabase.Companion.convertDatabaseIdToLocalId(context: Context, databaseId : Int, table : String) : Int{
    var id = -1
    val database = context.openOrCreateDatabase(DATA_BASE_NAME,Context.MODE_PRIVATE,null)
    if(database.isOpen){
        val curs = database.rawQuery("SELECT id FROM $table;",null)
        if(curs.moveToFirst()){
            id++
            if(databaseId == curs.getInt(0))return id
        }
        while(curs.moveToNext()){
            id++
            if(databaseId == curs.getInt(0))return id
        }
        curs.close()
        database.close()
    }
    return -1
}