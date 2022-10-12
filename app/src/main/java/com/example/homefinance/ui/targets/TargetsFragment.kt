package com.example.homefinance.ui.targets

import android.app.Activity
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.homefinance.AddTargetActivity
import com.example.homefinance.HomeFinanceDatabase
import com.example.homefinance.HomeFinanceTarget
import com.example.homefinance.R
import com.example.homefinance.usefullextesions.convertLocalIdToDatabaseId
import kotlin.contracts.contract

class HomeFinanceTargetAdapter(val context : Context, var targetsList : List<HomeFinanceTarget>) : RecyclerView.Adapter<HomeFinanceTargetAdapter.HomeFinanceTargetViewHolder>(){
    private var _selectedItem : MutableLiveData<View> = MutableLiveData<View>().apply { value = null }
    val selectedItem : LiveData<View> = _selectedItem
    inner class HomeFinanceTargetViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var targetImage : ImageView? = null
        var targetName : TextView? = null
        var targetStatus : TextView? = null
        var buttonFill : Button? = null
        var progressBar : ProgressBar? = null
        var editTextSum : EditText? = null
        var cardView : CardView? = null
        init {
            targetImage = itemView.findViewById(R.id.imageTarget)
            targetName = itemView.findViewById(R.id.textNameTarget)
            targetStatus = itemView.findViewById(R.id.textStatus)
            buttonFill = itemView.findViewById(R.id.buttonFill)
            cardView = itemView.findViewById(R.id.targetCard)
            progressBar = itemView.findViewById(R.id.progressTarget)
            editTextSum = itemView.findViewById(R.id.editEndSumTarget)
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): HomeFinanceTargetViewHolder {
        val itemView = LayoutInflater.from(p0.context).inflate(R.layout.homefinance_recycler_target_item,p0,false)
        return HomeFinanceTargetViewHolder(itemView)
    }

    override fun onBindViewHolder(p0: HomeFinanceTargetViewHolder, p1: Int) {
        if(targetsList[p1].photo == "no_photo") p0.targetImage?.setImageResource(R.drawable.ic_baseline_photo_camera_24)
        else p0.targetImage?.setImageURI(Uri.parse(targetsList[p1].photo))
        p0.targetName?.text = targetsList[p1].name
        p0.progressBar?.max = targetsList[p1].endSum.toInt()
        p0.progressBar?.setProgress(targetsList[p1].currentSum.toInt(),false)
        p0.buttonFill?.setOnClickListener{
            val parent = it.parent as ConstraintLayout
            val rec = parent.parent.parent.parent as RecyclerView
            val editTextSum = parent.findViewById<EditText>(R.id.editTextSumTarget)
            if(editTextSum.text.toString().toDouble() <= HomeFinanceDatabase.readBalance(this.context!!)){
                val target = HomeFinanceDatabase.readTargetData(this.context!!, HomeFinanceDatabase.convertLocalIdToDatabaseId(this.context, rec.getChildAdapterPosition(parent.parent.parent as LinearLayout), "Targets"))
                HomeFinanceDatabase.writeBalance(this.context!!,HomeFinanceDatabase.readBalance(this.context!!)-editTextSum.text.toString().toDouble())
                HomeFinanceDatabase.updateTargetSum(this.context!!, target?.currentSum!!.plus(editTextSum.text.toString().toDouble()),HomeFinanceDatabase.convertLocalIdToDatabaseId(this.context!!,p1,"Targets"))
                parent.findViewById<ProgressBar>(R.id.progressTarget).setProgress(target?.currentSum!!.plus(editTextSum.text.toString().toDouble()).toInt())
            //Toast.makeText(this.context!!,HomeFinanceDatabase.readBalance(this.context!!).toString(),Toast.LENGTH_LONG).show()
            }
            else Toast.makeText(this.context!!,"Недостаточно средств в бюджете семьи!",Toast.LENGTH_LONG).show()
        }
        p0.targetStatus?.text = if(targetsList[p1].endSum <= 0.0 || targetsList[p1].currentSum < targetsList[p1].endSum) context.resources.getString(R.string.text_in_progress) else context.resources.getString(R.string.text_complete)
        p0.buttonFill?.visibility = if(p0.targetStatus?.text == context.resources.getString(R.string.text_complete))View.INVISIBLE else View.VISIBLE
        p0.cardView?.setOnClickListener{
            _selectedItem.value = it
        }
    }

    override fun getItemCount(): Int {
        return targetsList.size
    }

    fun setNewData(newData : List<HomeFinanceTarget>){
        targetsList = newData
        this.notifyDataSetChanged()
    }
}


class TargetsFragment : Fragment() {

    companion object {
        fun newInstance() = TargetsFragment()
    }

    private lateinit var viewModel: TargetsViewModel
    private lateinit var root : View
    private lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_targets, container, false)
        viewModel = ViewModelProviders.of(this).get(TargetsViewModel::class.java)
        val fabAddTarget = root.findViewById<FloatingActionButton>(R.id.fabAddTarget)
        fabAddTarget.setOnClickListener{
            val intent = Intent(root.context!!,AddTargetActivity::class.java)
            startActivityForResult(intent,0)
        }
        recyclerView = root.findViewById(R.id.recyclerOfTargets)
        recyclerView.adapter = HomeFinanceTargetAdapter(root.context!!,HomeFinanceDatabase.readTargetsData(root.context!!))
        recyclerView.layoutManager = LinearLayoutManager(root.context!!)
        (recyclerView.adapter as HomeFinanceTargetAdapter).selectedItem.observe(viewLifecycleOwner,{
            val selectedCard = it as? CardView
            if(selectedCard != null){
                val intent = Intent(root.context,AddTargetActivity::class.java)
                intent.putExtra("keyIsEditing",true)
                intent.putExtra("keyClickedCardViewId",HomeFinanceDatabase.convertLocalIdToDatabaseId(
                        root.context,
                        recyclerView.getChildAdapterPosition((selectedCard?.parent as LinearLayout)),
                        "Targets"
                ))
                startActivityForResult(intent,1)
            }
        })
        viewModel.targets.observe(viewLifecycleOwner,{
            (recyclerView.adapter as HomeFinanceTargetAdapter).setNewData(it!!)
        })
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            viewModel.dataChaged()
        }
    }
}