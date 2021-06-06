package kr.ac.konkuk.koogle.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.ac.konkuk.koogle.Model.CardModel
import kr.ac.konkuk.koogle.databinding.ItemCardBinding

class CardAdapter: ListAdapter<CardModel, CardAdapter.ViewHolder>(diffUtil){

    inner class ViewHolder(private val binding: ItemCardBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(cardModel: CardModel){
            binding.nicknameTextView.text = cardModel.writerName
            binding.titleTextView.text = cardModel.articleTitle
            binding.contentTextView.text = cardModel.articleContent

            //glide 사용
            if(cardModel.writerProfileImageUrl.isNotEmpty()){
                Glide.with(binding.profileImageView)
                    .load(cardModel.writerProfileImageUrl)
                    .into(binding.profileImageView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCardBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object{
        val diffUtil = object : DiffUtil.ItemCallback<CardModel>() {
            override fun areItemsTheSame(oldModel: CardModel, newModel: CardModel): Boolean {
                return oldModel.articleId == newModel.articleId
            }

            override fun areContentsTheSame(oldModel: CardModel, newModel: CardModel): Boolean {
                return oldModel == newModel
            }
        }
    }
}
