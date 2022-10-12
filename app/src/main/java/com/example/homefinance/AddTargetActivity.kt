package com.example.homefinance

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.View
import android.widget.*

class AddTargetActivity : AppCompatActivity() {
    var isEditing : Boolean = false
    var photoUri : Uri = Uri.parse("no_photo")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_target)
        val imageButton = findViewById<ImageButton>(R.id.imageTargetButton)
        imageButton.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            if (intent.resolveActivity(packageManager!!) != null) {
                startActivityForResult(intent, 2)
            }
        }
        val deleteButton = findViewById<Button>(R.id.deleteButtonTarget)
        deleteButton.visibility = View.INVISIBLE
        isEditing = intent.getBooleanExtra("keyIsEditing",false)
        if(isEditing) {
            val editTargetName = findViewById<EditText>(R.id.editTargetName)
            val editCurrentSumTarget = findViewById<EditText>(R.id.editCurrentSumTarget)
            val editEndSumTarget = findViewById<EditText>(R.id.editEndSumTarget)
            val target = HomeFinanceDatabase.readTargetData(this,intent.getIntExtra("keyClickedCardViewId",-1))
            editTargetName.setText(target?.name)
            editCurrentSumTarget.setText(target?.currentSum.toString())
            editEndSumTarget.setText(target?.endSum.toString())
            if("no_photo" != target?.photo) {
                photoUri = Uri.parse(target?.photo)
                imageButton.setImageURI(photoUri)
            }
            deleteButton.visibility = View.VISIBLE
        }

        deleteButton.setOnClickListener{
            val target = HomeFinanceDatabase.readTargetData(this,intent.getIntExtra("keyClickedCardViewId",-1))
            if(target?.currentSum!! < target?.endSum!!) {
                HomeFinanceDatabase.writeBalance(this, HomeFinanceDatabase.readBalance(this) + target?.currentSum!!)
                Toast.makeText(this, "Цель удалена, средства возвращены в бюджет семьи", Toast.LENGTH_LONG).show()
                HomeFinanceDatabase.deleteTargetData(this, intent.getIntExtra("keyClickedCardViewId", -1))
            }
            else{
                if(target?.currentSum!! > target?.endSum!!) {
                    HomeFinanceDatabase.writeBalance(this, HomeFinanceDatabase.readBalance(this) + (target?.currentSum!! - target?.endSum!!))
                    Toast.makeText(this, "Цель удалена, остатки средств возвращены в бюджет.", Toast.LENGTH_LONG).show()
                }
                else
                    Toast.makeText(this, "Цель удалена.", Toast.LENGTH_LONG).show()
                HomeFinanceDatabase.deleteTargetData(this, intent.getIntExtra("keyClickedCardViewId", -1))
            }
            setResult(Activity.RESULT_OK)
            finish()
        }
        val cancelButton = findViewById<Button>(R.id.cancelButtonTarget)
        cancelButton.setOnClickListener{
            finish()
        }

        val buttonSave = findViewById<Button>(R.id.saveButtonTarget)
        buttonSave.setOnClickListener{
            val nameTarget = findViewById<EditText>(R.id.editTargetName)
            val currentSum = findViewById<EditText>(R.id.editCurrentSumTarget)
            val endSum = findViewById<EditText>(R.id.editEndSumTarget)
            if(!isEditing){
                HomeFinanceDatabase.writeTargetData(this,
                    HomeFinanceTarget(nameTarget.text.toString(), endSum.text.toString().toDouble(),currentSum.text.toString().toDouble(),photoUri.toString())
                )
                setResult(Activity.RESULT_OK)
                finish()
            }
            else{
                HomeFinanceDatabase.updateTargetData(this,HomeFinanceTarget(nameTarget.text.toString(), endSum.text.toString().toDouble(),currentSum.text.toString().toDouble(),photoUri.toString()),intent.getIntExtra("keyClickedCardViewId",-1))
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 2 && resultCode == Activity.RESULT_OK) {
            if(data?.data == null){
                photoUri = Uri.parse("no_photo")
            }
            else {
                photoUri = data?.data!!
                findViewById<ImageButton>(R.id.imageTargetButton).setImageURI(photoUri)
            }
        }
    }
}