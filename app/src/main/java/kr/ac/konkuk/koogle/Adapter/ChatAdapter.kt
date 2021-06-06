package kr.ac.konkuk.koogle.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.ac.konkuk.koogle.Model.ChatModel
import kr.ac.konkuk.koogle.databinding.ItemChatBinding

class ChatAdapter: ListAdapter<ChatModel, ChatAdapter.ViewHolder>(diffUtil){
    inner class ViewHolder(private val binding: ItemChatBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(chatModel: ChatModel) {
            binding.senderTextView.text = chatModel.senderId
            binding.messageTextView.text = chatModel.message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //context 는 parent 에 있다
        return ViewHolder(ItemChatBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    //todo DiffUtil을 사용하기 위해선 고유 키값이 존재 해야 함

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatModel>() {
            override fun areItemsTheSame(oldModel: ChatModel, newModel: ChatModel): Boolean {
                return oldModel == newModel
            }

            override fun areContentsTheSame(oldModel: ChatModel, newModel: ChatModel): Boolean {
                return oldModel == newModel
            }
        }
    }

}