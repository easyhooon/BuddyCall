package kr.ac.konkuk.koogle.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import kr.ac.konkuk.koogle.DBKeys.Companion.LEFT_CHAT
import kr.ac.konkuk.koogle.DBKeys.Companion.RIGHT_CHAT
import kr.ac.konkuk.koogle.Model.ChatModel
import kr.ac.konkuk.koogle.databinding.ItemLeftChatBinding
import kr.ac.konkuk.koogle.databinding.ItemRightChatBinding
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(val context: Context, private val chatList: MutableList<ChatModel>) :
    RecyclerView.Adapter<ChatAdapter.BaseViewHolder<*>>() {

    abstract class BaseViewHolder<T>(private val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(item: T)
    }

    inner class LeftViewHolder(private val binding: ItemLeftChatBinding) : BaseViewHolder<ChatModel>(binding) {

        @SuppressLint("SimpleDateFormat")
        val format = SimpleDateFormat("a HH:mm", Locale.KOREA)

        override fun bind(chatModel: ChatModel) {
            val time = Date(chatModel.chatCreatedAt)

            binding.writerNameTextView.text = chatModel.writerName
            binding.contentTextView.text = chatModel.chatContent
            binding.timeTextView.text = format.format(time).toString()

            if (chatModel.writerProfileImageUrl.isNotEmpty()) {
                Glide.with(binding.writerProfileImage)
                    .load(chatModel.writerProfileImageUrl)
                    .into(binding.writerProfileImage)
            }
        }
    }

    inner class RightViewHolder(private val binding: ItemRightChatBinding) : BaseViewHolder<ChatModel>(binding) {

        @SuppressLint("SimpleDateFormat")
        val format = SimpleDateFormat("a HH:mm", Locale.KOREA)

        override fun bind(chatModel: ChatModel) {
            val time = Date(chatModel.chatCreatedAt)

            binding.contentTextView.text = chatModel.chatContent
            binding.timeTextView.text = format.format(time).toString()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        //context 는 parent 에 있다
        return when (viewType) {
            LEFT_CHAT -> {
                LeftViewHolder(ItemLeftChatBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            }
            RIGHT_CHAT -> {
                RightViewHolder(ItemRightChatBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }

    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element = chatList[position]
        when (holder) {
            is LeftViewHolder -> {
                holder.bind(element)
            }

            is RightViewHolder -> {
                holder.bind(element)
            }
            else -> throw IllegalArgumentException()
        }

    }

    override fun getItemViewType(position: Int): Int {
        return chatList[position].viewType
    }


    override fun getItemCount(): Int {
        return chatList.size
    }

}