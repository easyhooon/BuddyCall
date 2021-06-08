package kr.ac.konkuk.koogle.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.ac.konkuk.koogle.Model.ChatModel
import kr.ac.konkuk.koogle.databinding.ItemChatBinding
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter: ListAdapter<ChatModel, ChatAdapter.ViewHolder>(diffUtil){
    inner class ViewHolder(private val binding: ItemChatBinding): RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SimpleDateFormat")
        fun bind(chatModel: ChatModel) {
            val format = SimpleDateFormat("a HH:mm", Locale.KOREA)
            val time = Date(chatModel.chatCreatedAt)

            binding.writerNameTextView.text = chatModel.writerName
            binding.contentTextView.text = chatModel.chatContent
            binding.timeTextView.text = format.format(time).toString()

            if(chatModel.writerProfileImageUrl.isNotEmpty()){
                Glide.with(binding.writerProfileImage)
                    .load(chatModel.writerProfileImageUrl)
                    .into(binding.writerProfileImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //context 는 parent 에 있다
        return ViewHolder(ItemChatBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatModel>() {
            override fun areItemsTheSame(oldModel: ChatModel, newModel: ChatModel): Boolean {
                return oldModel.chatCreatedAt == newModel.chatCreatedAt
            }

            override fun areContentsTheSame(oldModel: ChatModel, newModel: ChatModel): Boolean {
                return oldModel == newModel
            }
        }
    }

}