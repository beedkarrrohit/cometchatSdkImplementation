package com.example.cometchatprotask.cometchatactivities.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cometchat.pro.models.User
import com.example.cometchatprotask.R
import com.example.cometchatprotask.databinding.RecyclerItemRowBinding


class UserListAdapter(private val onClickInterface: OnClickInterface) : ListAdapter<User,UserListAdapter.UserViewHOlder>(comparator) {

    class UserViewHOlder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val binding = RecyclerItemRowBinding.bind(itemView)
        companion object{
            fun create(parent: ViewGroup) : UserViewHOlder{
                val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_row,parent,false)
                return UserViewHOlder(view)
            }
        }
        fun bind(current : User,onClickInterface: OnClickInterface){
            binding.usersName.text = current.name
            binding.status.text = current.status
            Glide.with(itemView).load(current.avatar).placeholder(R.drawable.user).into(binding.avatar)
            binding.itemRow.setOnClickListener {
                onClickInterface.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHOlder {
        return UserViewHOlder.create(parent)
    }

    override fun onBindViewHolder(holder: UserViewHOlder, position: Int) {
        val current = getItem(position)

        holder.bind(current,onClickInterface)
    }

    companion object{
        private val comparator = object : DiffUtil.ItemCallback<User>(){
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.uid.equals(newItem.uid)
            }
        }

    }
}