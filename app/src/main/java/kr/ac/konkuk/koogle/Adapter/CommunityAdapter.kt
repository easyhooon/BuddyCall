package kr.ac.konkuk.koogle.Adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.ac.konkuk.koogle.Model.ArticleModel
import kr.ac.konkuk.koogle.databinding.ItemArticleBinding
import java.text.SimpleDateFormat
import java.util.*

class CommunityAdapter(val onItemClicked: (ArticleModel) -> Unit): ListAdapter<ArticleModel, CommunityAdapter.ViewHolder>(diffUtil){
    inner class ViewHolder(private val binding: ItemArticleBinding): RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SimpleDateFormat")
        fun bind(articleModel: ArticleModel) {
            val format = SimpleDateFormat("MM월 dd일")
            val date = Date(articleModel.articleCreatedAt)
            //createAt으로 현재 시간을 long타입으로 받아왔는데 그것을 Date타입으로 바꾼다음에
            //simpleDateFormat을 통해 포매팅 완성
            binding.writerNameTextView.text = articleModel.writerName
            binding.titleTextView.text = articleModel.articleTitle
            binding.dateTextView.text = format.format(date).toString()
            binding.recruitmentNumberTextView.text = articleModel.recruitmentNumber.toString()
            //현재 그룹에 속한 사람의 수를 계산하여 넣음
            binding.currentNumberTextView.text = articleModel.currentNumber.toString()
            binding.contentTextView.text = articleModel.articleContent

            if(articleModel.writerProfileImageUrl.isNotEmpty()){
                Glide.with(binding.writerProfileImage)
                    .load(articleModel.writerProfileImageUrl)
                    .into(binding.writerProfileImage)
            }
            binding.root.setOnClickListener {
                onItemClicked(articleModel)
            }

            if(articleModel.currentNumber == articleModel.recruitmentNumber){
                binding.currentNumberTextView.setTextColor(Color.RED)
                binding.slash.setTextColor(Color.RED)
                binding.recruitmentNumberTextView.setTextColor(Color.RED)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //context는 parent에 있다
        return ViewHolder(ItemArticleBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ArticleModel>() {
            override fun areItemsTheSame(oldModel: ArticleModel, newModel: ArticleModel): Boolean {
                return oldModel.articleId == newModel.articleId
            }

            override fun areContentsTheSame(oldModel: ArticleModel, newModel: ArticleModel): Boolean {
                return oldModel == newModel
            }
        }
    }
}

