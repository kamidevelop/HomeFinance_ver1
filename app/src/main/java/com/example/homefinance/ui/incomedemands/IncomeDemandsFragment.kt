package com.example.homefinance.ui.incomedemands

import android.app.Activity
import android.arch.lifecycle.*
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v4.app.Fragment
import android.content.Context
import android.content.Intent
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ViewParent
import android.view.accessibility.AccessibilityManager
import android.widget.*
import com.example.homefinance.*
import com.example.homefinance.R
import com.example.homefinance.ui.displaydemand.DisplayDemandFragment
import com.example.homefinance.ui.displaydemand.DisplayDemandViewModel
import com.example.homefinance.ui.displayincome.DisplayIncomeFragment
import com.example.homefinance.ui.displayincome.DisplayIncomeViewModel
import com.example.homefinance.ui.family.HomeFinancePersonAdaptedData
import com.example.homefinance.usefullextesions.convertLocalIdToDatabaseId
import com.example.homefinance.usefullextesions.getChildsOfType
import org.w3c.dom.Text
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

val INCOME_FRAGMENT = 0
val DEMAND_FRAGMENT = 1

class HomeFinanceIncomeDemandsViewPagerAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm){
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(p0: Int): Fragment {
        return when(p0){
            0 -> return DisplayIncomeFragment()
            else -> return DisplayDemandFragment()
        }
    }

}

class IncomeDemandsFragment : Fragment() {

    private lateinit var dateSelected : String
    private lateinit var calendar : CalendarView
    private lateinit var viewPager : ViewPager
    private lateinit var root : View
    private var selectedView : CardView? = null
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_incomes_demands, container, false)
        dateSelected = SimpleDateFormat("dd-MM-yyyy").format(Date())
        calendar = root.findViewById(R.id.calendarView)
        viewPager = root.findViewById(R.id.viewPagerOfFragments)
        viewPager.adapter = HomeFinanceIncomeDemandsViewPagerAdapter(childFragmentManager)
        viewPager.currentItem = 0
        calendar.setOnDateChangeListener(CalendarView.OnDateChangeListener { view, year, month, dayOfMonth ->
            val args : Bundle = Bundle()
            args.putString("keyDate",dayOfMonth.toString()+"-"+(month+1).toString()+"-"+year.toString())
            dateSelected = args.getString("keyDate", SimpleDateFormat("dd-MM-yyyy").format(Date()))
            val viewPg = (view.parent as View).findViewById<ViewPager>(R.id.viewPagerOfFragments)
            val adapter = viewPg.adapter as HomeFinanceIncomeDemandsViewPagerAdapter
            val getedOne = childFragmentManager.findFragmentByTag("android:switcher:"+R.id.viewPagerOfFragments+":"+viewPg.currentItem)
            if(getedOne is DisplayIncomeFragment) {
                val displayIncomeFragment = getedOne as DisplayIncomeFragment
                //displayIncomeFragment.onCreate(null)
                displayIncomeFragment.updateDisplayedData(args)
            }
            else {
                val displayDemandFragment = getedOne as DisplayDemandFragment
                displayDemandFragment.updateDisplayedData(args)
            }
        })
        val floatingButton = root.findViewById<FloatingActionButton>(R.id.floatingButtonAddIncomeDemands)
        floatingButton.setOnClickListener{
            Toast.makeText(this.context!!,dateSelected,Toast.LENGTH_LONG).show()
            val intent = Intent(root.context, AddIncomeDemands::class.java)
            intent.putExtra("keyTitleDate",dateSelected)
            startActivityForResult(intent,0)
        }
        return root
    }

    override fun onResume() {
        super.onResume()
      //  activity?.intent!!.putExtra("keyDate",dateSelected)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            val adapter = (viewPager.adapter as HomeFinanceIncomeDemandsViewPagerAdapter)
            //val selectedDate = calendar.date
            //val calendarInstance = Calendar.getInstance()
            //calendarInstance.timeInMillis = selectedDate

        }
    }
}