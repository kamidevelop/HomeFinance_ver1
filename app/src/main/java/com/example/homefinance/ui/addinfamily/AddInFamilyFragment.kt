package com.example.homefinance.ui.addinfamily

import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.app.Activity
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityManager
import android.widget.*
import com.example.homefinance.HomeFinanceDatabase
import com.example.homefinance.HomeFinancePerson
import com.example.homefinance.R
import org.w3c.dom.Text

class AddInFamilyFragment : Fragment() {

    companion object {
        fun newInstance() = AddInFamilyFragment()
    }

    private lateinit var viewModel: AddInFamilyViewModel
    private lateinit var root : View
    private var isEditing : Boolean = false;
    private var photoUri : Uri = Uri.parse("no_photo")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.add_in_family_fragment, container, false)
        var categoryField = root.findViewById<EditText>(R.id.categoryDemandsEditText)
        categoryField.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean{
                if(event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER){
                    addCategoryOfDemands((v as EditText).text.toString())
                    return true
                }
                else return false
            }
        })

        var addImageButton = root.findViewById<ImageButton>(R.id.imageButtonAddPhoto)
        addImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            if (intent.resolveActivity(activity?.packageManager!!) != null) {
                startActivityForResult(intent, 2)
            }
        }
        isEditing = activity?.intent!!.getBooleanExtra("keyIsEditing",false)
        var deleteButton = root.findViewById<Button>(R.id.deleteButton)
        deleteButton.setOnClickListener {
            HomeFinanceDatabase.deleteFamilyData(this.context!!, activity?.intent!!.getIntExtra("keyClickedCardViewId", -1))
            val intent = Intent()
            intent.putExtra("keyIsDataDeleted", true)
            activity?.setResult(Activity.RESULT_OK, intent)
            activity?.finish()
        }
        deleteButton.visibility = View.INVISIBLE
        if(isEditing){
            var imageButton = root.findViewById<ImageButton>(R.id.imageButtonAddPhoto)
            var nameFIO = root.findViewById<EditText>(R.id.editTextTextPersonName)
            var sourceOfIncome = root.findViewById<EditText>(R.id.sourceOfIncomeEditText)
            var coinbox = root.findViewById<EditText>(R.id.coinboxDecimalNumber)
            val wallet = root.findViewById<EditText>(R.id.editTextWallet)
            val person = HomeFinanceDatabase.readFamilyPersonData(root.context,activity?.intent!!.getIntExtra("keyClickedCardViewId",-1))
            photoUri = Uri.parse(person?.photo)
            if(photoUri.toString() != "no_photo") {
                imageButton.setImageURI(Uri.parse(person?.photo))
                photoUri = Uri.parse(person?.photo)
            }
            nameFIO.setText(person?.name)
            activity?.title = nameFIO.text
            sourceOfIncome.setText(person?.sourceOfIncome)
            coinbox.setText(person?.coinBox.toString())
            wallet.setText(person?.wallet.toString())
            val list = person?.categoriesOfDemands!!.split(",")
            for(category in list){
                addCategoryOfDemands(category.replace("'",""))
            }
            deleteButton.visibility = View.VISIBLE
        }
        val buttonCancel = root.findViewById<Button>(R.id.cancelButton)
        buttonCancel.setOnClickListener {
            activity?.finish()
        }
        val buttonSave = root.findViewById<Button>(R.id.saveDataButton)
        buttonSave.setOnClickListener {
            val nameFIO: String = root.findViewById<EditText>(R.id.editTextTextPersonName).text.toString()
            val sourceOfIncome: String = root.findViewById<EditText>(R.id.sourceOfIncomeEditText).text.toString()
            val coinbox: Double = root.findViewById<EditText>(R.id.coinboxDecimalNumber).text.toString().toDouble()
            val wallet: Double = root.findViewById<EditText>(R.id.editTextWallet).text.toString().toDouble()
            val categoriesOfDemads = run {
                val layOfCategories = root.findViewById<LinearLayout>(R.id.layoutOfCategories)
                var categories: String = ""
                for (i in 0..layOfCategories.childCount - 1) {
                    categories += "'" + (layOfCategories.getChildAt(i) as? TextView)?.text.toString() + "'"
                    if (i < layOfCategories.childCount - 1) categories += ","
                }
                return@run categories
            }
            val person = HomeFinancePerson(nameFIO, categoriesOfDemads, sourceOfIncome, coinbox, wallet, photoUri.toString())
            if (!isEditing) {
                HomeFinanceDatabase.writeFamilyData(root.context, person)
                val intent = Intent()
                activity?.setResult(Activity.RESULT_OK, intent)
                activity?.finish()
            } else {
                HomeFinanceDatabase.updateFamilyData(root.context, activity?.intent!!.getIntExtra("keyClickedCardViewId", -1), person)
                val intent = Intent()
                activity?.setResult(Activity.RESULT_OK, intent)
                activity?.finish()
            }
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(AddInFamilyViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 2 && resultCode == Activity.RESULT_OK) {
            if(data?.data == null){
                photoUri = Uri.parse("no_photo")
            }
            else {
                photoUri = data?.data!!
                root.findViewById<ImageButton>(R.id.imageButtonAddPhoto).setImageURI(photoUri)
            }
        }
    }

    fun addCategoryOfDemands(category : String){
        var text : TextView = TextView(this.context)
        val layoutCategories = root.findViewById<LinearLayout>(R.id.layoutOfCategories)
        val categoryInputField = root.findViewById<EditText>(R.id.categoryDemandsEditText)
        text.text = category
        text.isClickable = true
        text.isFocusable = true
        text.setOnClickListener(View.OnClickListener {
            if(categoryInputField.text.isEmpty()) {
                categoryInputField.setText((it as TextView).text)
                layoutCategories.removeView(it)
            }
            else{
                    val strCategory = (it as TextView).text
                    (it as TextView).text = categoryInputField.text
                categoryInputField.setText(strCategory)
            }
        })
        layoutCategories.addView(text);
        root.findViewById<EditText>(R.id.categoryDemandsEditText).setText("")
    }

}