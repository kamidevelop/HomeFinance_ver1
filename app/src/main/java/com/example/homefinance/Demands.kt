package com.example.homefinance

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.homefinance.usefullextesions.getCategoriesFromCategoryString

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Demands.newInstance] factory method to
 * create an instance of this fragment.
 */
class Demands : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var root : View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_demands, container, false)
        root.findViewById<Spinner>(R.id.spinnerFrom).adapter = ArrayAdapter.createFromResource(this.context!!,R.array.items_containers,R.layout.homefinance_spinner_item)
        root.findViewById<Spinner>(R.id.spinnerWhose).adapter = HomeFinanceSpinnerAdapter(this.context!!, R.layout.homefinance_spinner_item,HomeFinanceDatabase.readFamilyData(this.context!!).run{
            val listOfNames : MutableList<String> = emptyList<String>().toMutableList()
            for(name in this){
                listOfNames.add(name.name)
            }
            return@run listOfNames.toTypedArray()
        })
        root.findViewById<Spinner>(R.id.spinnerOfCategory).adapter = HomeFinanceSpinnerAdapter(this.context!!, R.layout.homefinance_spinner_item, HomeFinanceDatabase.readFamilyData(this.context!!).run {
            var arrayOfCategories : Array<String> = emptyArray()
            for(person in this){
                var arrCategory = HomeFinanceDatabase.getCategoriesFromCategoryString(person).toTypedArray()
                arrayOfCategories = arrayOfCategories.copyOf(arrayOfCategories.size+arrCategory.size) as Array<String>
                arrayOfCategories = arrCategory.copyInto(arrayOfCategories)
            }
            arrayOfCategories = arrayOfCategories.copyOfRange(1,arrayOfCategories.size)
            return@run arrayOfCategories
        })
        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Demands.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Demands().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}