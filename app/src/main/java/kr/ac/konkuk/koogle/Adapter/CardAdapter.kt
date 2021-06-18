package kr.ac.konkuk.koogle.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.ac.konkuk.koogle.Model.ArticleModel
import kr.ac.konkuk.koogle.Model.CardModel
import kr.ac.konkuk.koogle.databinding.ItemCardBinding

class CardAdapter: ListAdapter<CardModel, CardAdapter.ViewHolder>(diffUtil){

    //리스너 정의
    interface OnItemClickListener {
        //호출할 함수 명시 (입력 정보를 담아서, 뷰홀더, 뷰, 데이터, 포지션)
        fun onItemChecked(holder: ViewHolder, view: View, data: CardModel, position: Int)

        //이 것을 인터페이스로 구현하는 객체가 있는데 그 객체가 구현한 함수를 호출한다는 것을 의미
        fun onItemCanceled(holder: ViewHolder, view: View, data: CardModel, position: Int)
    }

    //인터페이스를 맴버로 선언
    var itemClickListener: OnItemClickListener? = null

    inner class ViewHolder(private val binding: ItemCardBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(cardModel: CardModel){
            binding.nicknameTextView.text = cardModel.writerName
            binding.titleTextView.text = cardModel.articleTitle
            binding.contentTextView.text = cardModel.articleContent
            binding.recruitmentNumberTextView.text = cardModel.recruitmentNumber.toString()
            binding.currentNumberTextView.text = cardModel.currentNumber.toString()
            binding.locationTextView.text = cardModel.desiredLocation?.fullAddress ?: ""

            //glide 사용
            if(cardModel.writerProfileImageUrl.isNotEmpty()){
                Glide.with(binding.profileImageView)
                    .load(cardModel.writerProfileImageUrl)
                    .into(binding.profileImageView)
            }
            if(cardModel.currentNumber == cardModel.recruitmentNumber){
                binding.currentNumberTextView.setTextColor(Color.RED)
                binding.slash.setTextColor(Color.RED)
                binding.recruitmentNumberTextView.setTextColor(Color.RED)
            }

            //todo 썸네일 이미지 뷰를 글라이드를 통해 로드
            //todo 사진이 존재하지 않는다면 해당 이미지뷰가 보이지 않게

            binding.checkImageView.setOnClickListener {
                itemClickListener?.onItemChecked(
                    this,
                    it,
                    currentList[adapterPosition],
                    adapterPosition
                )
            }

            binding.cancelImageView.setOnClickListener {
                itemClickListener?.onItemCanceled(
                    this,
                    it,
                    currentList[adapterPosition],
                    adapterPosition
                )
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
