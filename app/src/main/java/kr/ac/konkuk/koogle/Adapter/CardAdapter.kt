package kr.ac.konkuk.koogle.Adapter

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.setMargins
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.ac.konkuk.koogle.DBKeys
import kr.ac.konkuk.koogle.Model.ArticleModel
import kr.ac.konkuk.koogle.Model.CardModel
import kr.ac.konkuk.koogle.Model.TagModel
import kr.ac.konkuk.koogle.R
import kr.ac.konkuk.koogle.databinding.ItemCardBinding

// 2021-06-20 주예진 수정: context 추가(tag TextView를 동적으로 추가시켜주기 위해 사용)
class CardAdapter(val context: Context?): ListAdapter<CardModel, CardAdapter.ViewHolder>(diffUtil){

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
        var tagView: LinearLayout = itemView.findViewById(R.id.tagView)
        fun bind(cardModel: CardModel){
            binding.nicknameTextView.text = cardModel.writerName
            binding.titleTextView.text = cardModel.articleTitle
            binding.contentTextView.text = cardModel.articleContent
            binding.recruitmentNumberTextView.text = cardModel.recruitmentNumber.toString()
            binding.currentNumberTextView.text = cardModel.currentNumber.toString()
            binding.locationTextView.text = cardModel.desiredLocation?.fullAddress ?: ""

            // Tag List
            if(cardModel.tagList!=null){
                var newTagList = arrayListOf<String>()
                
                // 데이타 파싱
                for(t in cardModel.tagList!!.children){
                    for(st in t.child(DBKeys.SUB_TAGS).children){
                        newTagList.add(st.key.toString())
                    }
                }
                // 뷰에 적용
                settingTagDataToView(newTagList)
            }

            //glide 사용
            if(cardModel.writerProfileImageUrl.isNotEmpty()){
                Glide.with(binding.profileImageView)
                    .load(cardModel.writerProfileImageUrl)
                    .into(binding.profileImageView)
            }
            if(cardModel.articleThumbnailImageUrl.isNotEmpty()){
                Glide.with(binding.cardThumbnailBackground)
                    .load(cardModel.articleThumbnailImageUrl)
                    .into(binding.cardThumbnailBackground)
                binding.cardThumbnailBackground.alpha = 0.5f
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
        private fun getRow(index: Int): LinearLayout{
            return (tagView.getChildAt(index) as ScrollView).getChildAt(0) as LinearLayout
        }
        // 태그 row 추가
        private fun addRow() {
            val row = LinearLayout(context)
            val lp: LinearLayout.LayoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            row.orientation = LinearLayout.HORIZONTAL
            row.layoutParams = lp

            val newScrollView = ScrollView(context)

            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            newScrollView.layoutParams = layoutParams

            val linearParams = LinearLayout.LayoutParams(
                800,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            row.orientation = LinearLayout.HORIZONTAL
            row.layoutParams = linearParams

            newScrollView.addView(row)
            tagView.addView(newScrollView)
        }
        // 태그 리스트를 여러 줄로 분할해 동적으로 생성
        private fun settingTagDataToView(data: ArrayList<String>){
            var lastRow: LinearLayout
            for (tag in data) {
                // row 가 하나도 없으면 새로 만들기
                if (tagView.childCount == 0) {
                    addRow()
                }
                // 새로운 Table row 를 추가해야 하는지 길이 검사
                lastRow = getRow(tagView.childCount - 1)
                var len = 0
                val row_len = 26
                val margin = 1
                for (i in lastRow.children) {
                    i as TextView
                    len += i.text.length + margin
                }
                len += tag.length
                if (len > row_len) {
                    addRow()
                }
                lastRow = getRow(tagView.childCount - 1)

                lastRow.addView(makeSubTagView(tag))
            }
        }
        // SubTag 한 칸을 생성한다.
        fun makeSubTagView(tagName: String): TextView {
            var subTagText = TextView(context)
            subTagText.setText(tagName)
            // 모서리가 둥근 태그 스타일 적용(임시)
            subTagText.setTextAppearance(R.style.TAG_STYLE)
            subTagText.setBackgroundResource(R.drawable.layout_tag_background)
            // 태그 간 간격 설정
            val p = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            p.setMargins(5)
            subTagText.layoutParams = p
            return subTagText
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
