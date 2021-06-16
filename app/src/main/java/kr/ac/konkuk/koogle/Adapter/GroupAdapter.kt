package kr.ac.konkuk.koogle.Adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.ac.konkuk.koogle.Model.GroupModel
import kr.ac.konkuk.koogle.databinding.ItemGroupBinding
import java.text.SimpleDateFormat
import java.util.*

class GroupAdapter(val onItemClicked: (GroupModel) -> Unit): ListAdapter<GroupModel, GroupAdapter.ViewHolder>(diffUtil){
    inner class ViewHolder(private val binding: ItemGroupBinding): RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SimpleDateFormat")
        val format = SimpleDateFormat("a HH:mm", Locale.KOREA)
        fun bind(groupModel: GroupModel) {
            binding.titleTextView.text = groupModel.articleTitle
            binding.lastChatTextView.text = groupModel.groupLastChat
            binding.currentNumberTextView.text = groupModel.currentNumber.toString()
            if(groupModel.groupLastChatCreatedAt == 0L){
                binding.lastChatCreatedAtTextView.text = ""
            }
            else {
                val time = Date(groupModel.groupLastChatCreatedAt)
                binding.lastChatCreatedAtTextView.text = format.format(time).toString()
            }
            if(groupModel.adminProfileImageUrl.isNotEmpty()){
                Glide.with(binding.adminProfileImageView)
                    .load(groupModel.adminProfileImageUrl)
                    .into(binding.adminProfileImageView)
            }
            binding.root.setOnClickListener {
                onItemClicked(groupModel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //context 는 parent 에 있다
        //parent 는 ViewGroup
        return ViewHolder(ItemGroupBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<GroupModel>() {
            override fun areItemsTheSame(oldModel: GroupModel, newModel: GroupModel): Boolean {
                return oldModel.groupId == newModel.groupId
            }

            override fun areContentsTheSame(oldModel: GroupModel, newModel: GroupModel): Boolean {
                return oldModel == newModel
            }
        }
    }

}