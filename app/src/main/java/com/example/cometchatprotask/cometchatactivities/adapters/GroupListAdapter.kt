package com.example.cometchatprotask.cometchatactivities.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cometchat.pro.models.Group
import com.example.cometchatprotask.R
import com.example.cometchatprotask.databinding.RecyclerItemRowBinding


class GroupListAdapter(private val onClickInterface: OnClickInterface) : ListAdapter<Group,GroupListAdapter.GroupViewHolder>(comparator) {
    class GroupViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val binding = RecyclerItemRowBinding.bind(itemView)
        companion object{
             fun create(parent : ViewGroup) : GroupViewHolder{
                val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_row,parent,false)
                return GroupViewHolder(view)
            }
        }
        fun bind(current : Group, onClickInterface: OnClickInterface){
            binding.usersName.text = current.name
            Glide.with(itemView).load(current.icon).placeholder(R.drawable.user).into(binding.avatar)
            binding.status.text = "Members: ${current.membersCount}"
            binding.itemRow.setOnClickListener {
                onClickInterface.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        return GroupViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(getItem(position),onClickInterface)
    }
    companion object{
        private val comparator = object : DiffUtil.ItemCallback<Group>(){
            override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
                return oldItem.guid.equals(newItem.guid)
            }

        }
    }
}