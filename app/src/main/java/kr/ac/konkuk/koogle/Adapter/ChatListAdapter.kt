package kr.ac.konkuk.koogle.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.ac.konkuk.koogle.Model.ChatListModel
import kr.ac.konkuk.koogle.databinding.ItemChatListBinding

class ChatListAdapter(val onItemClicked: (ChatListModel) -> Unit): ListAdapter<ChatListModel, ChatListAdapter.ViewHolder>(diffUtil){
    inner class ViewHolder(private val binding: ItemChatListBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(chatListModel: ChatListModel) {
            binding.root.setOnClickListener {
                onItemClicked(chatListModel)
            }
            binding.chatRoomTitleTextView.text = chatListModel.itemTitle
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //context 는 parent 에 있다
        //parent 는 ViewGroup
        return ViewHolder(ItemChatListBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatListModel>() {
            override fun areItemsTheSame(oldModel: ChatListModel, newModel: ChatListModel): Boolean {
                return oldModel.key == newModel.key
            }

            override fun areContentsTheSame(oldModel: ChatListModel, newModel: ChatListModel): Boolean {
                return oldModel == newModel
            }
        }
    }

}