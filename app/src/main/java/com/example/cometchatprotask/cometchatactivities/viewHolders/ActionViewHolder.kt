package com.example.cometchatprotask.cometchatactivities.viewHolders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cometchat.pro.models.Action
import com.example.cometchatprotask.R
import com.example.cometchatprotask.databinding.ActionItemRowBinding

class ActionViewHolder(itemView : View) : BaseViewHolder(itemView) {
    companion object{
        lateinit var binding : ActionItemRowBinding
        fun create(parent : ViewGroup):ActionViewHolder{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.action_item_row,parent,false)
           binding = ActionItemRowBinding.bind(view)
           return ActionViewHolder(view)
        }
    }

    fun bind(action : Action){
        binding.actionMesssage.text = action.message
    }
}