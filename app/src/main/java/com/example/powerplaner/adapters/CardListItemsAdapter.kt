package com.example.powerplaner.adapters

import android.content.Context
import android.graphics.Color

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.powerplaner.Activities.TaskListActivity
import com.example.powerplaner.Models.Card
import com.example.powerplaner.Models.SelectedMembers
import com.example.powerplaner.R
import kotlinx.android.synthetic.main.item_card.view.*

open class CardListItemsAdapter(
        private val context: Context,
        private var list: ArrayList<Card>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.item_card,
                        parent,
                        false
                )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            
            if(model.labelColor.isNotEmpty()){
                holder.itemView.view_label_color.visibility = View.VISIBLE
                holder.itemView.view_label_color.setBackgroundColor(Color.parseColor(model.labelColor))
            }else{
                holder.itemView.view_label_color.visibility = View.GONE

            }
            holder.itemView.tv_card_name.text = model.name

            if((context as TaskListActivity).mAssignedMembersDetailList.size>0){
                val selectedMemberList:ArrayList<SelectedMembers> =ArrayList()
                for(i in context.mAssignedMembersDetailList.indices){
                    for(j in model.assignedTo){
                        if(context.mAssignedMembersDetailList[i].id==j){
                            val selectedMembers = SelectedMembers(
                                    context.mAssignedMembersDetailList[i].id,
                                    context.mAssignedMembersDetailList[i].image
                            )
                            selectedMemberList.add(selectedMembers)
                        }
                    }
                }
                if(selectedMemberList.size>0){
                    if(selectedMemberList.size==1&&selectedMemberList[0].id==model.createdBy){
                        holder.itemView.rv_card_selected_members_list.visibility=View.GONE
                    }else{
                        holder.itemView.rv_card_selected_members_list.visibility=View.VISIBLE

                        holder.itemView.rv_card_selected_members_list.layoutManager= GridLayoutManager(context,4)
                        val adapter=CardMemberListItemsAdapter(context,selectedMemberList,false)
                        holder.itemView.rv_card_selected_members_list.adapter=adapter

                        adapter.setOnClickListener(
                                object:CardMemberListItemsAdapter.OnClickListener {
                                    override fun onClick(){
                                        if(onClickListener!=null){
                                            onClickListener!!.onClick(position)
                                        }
                                    }
                                }
                        )


                    }
                }else{
                    holder.itemView.rv_card_selected_members_list.visibility=View.GONE
                }
            }

            holder.itemView.setOnClickListener {
                if(onClickListener!=null){
                    onClickListener!!.onClick(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int)
    }
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}