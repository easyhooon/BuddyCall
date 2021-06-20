package kr.ac.konkuk.koogle.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.ac.konkuk.koogle.Model.CommentModel
import kr.ac.konkuk.koogle.R
import kr.ac.konkuk.koogle.databinding.ItemCommentBinding
import java.text.SimpleDateFormat
import java.util.*

/*
    2021-05-27 주예진 작성
    프로필에서 표시되는 후기 목록 Recycler View 의 row adapter
    유저 프로필 이미지, 유저네임, 리뷰 내용을 표시
    현재 임시로 데이터를 문자열로 받아오게 구현함 수정 필요
 */
//class RecommendAdapter(val context: Context, val items: ArrayList<ArrayList<String>>) :
//    RecyclerView.Adapter<RecommendAdapter.ViewHolder>() {
//
//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var recommendUserNameView: TextView = itemView.findViewById(R.id.recommendUserNameText)
//        var recommendTextView: TextView = itemView.findViewById(R.id.recommendText)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendAdapter.ViewHolder {
//        val view = LayoutInflater.from(context).inflate(R.layout.row_recommend, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: RecommendAdapter.ViewHolder, position: Int) {
//        holder.recommendUserNameView.text = items[position][0]
//        holder.recommendTextView.text = items[position][1]
//    }
//
//    override fun getItemCount(): Int {
//        return items.size
//    }
//}

class CommentAdapter: ListAdapter<CommentModel, CommentAdapter.ViewHolder>(diffUtil){
    inner class ViewHolder(private val binding: ItemCommentBinding): RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SimpleDateFormat")
        fun bind(commentModel: CommentModel) {
            val format = SimpleDateFormat("MM월 dd일")
            val date = Date(commentModel.commentCreatedAt)
            //createAt으로 현재 시간을 long타입으로 받아왔는데 그것을 Date타입으로 바꾼다음에
            //simpleDateFormat을 통해 포매팅 완성
            binding.recommendUserNameTextView.text = commentModel.writerName
            binding.recommendCreatedAtTextView.text = format.format(date).toString()
            binding.recommendContentTextView.text = commentModel.commentContent

            if(commentModel.writerProfileImageUrl.isNotEmpty()){
                Glide.with(binding.recommendUserProfileImage)
                    .load(commentModel.writerProfileImageUrl)
                    .into(binding.recommendUserProfileImage)
            }
            else{
                binding.recommendUserProfileImage.setImageResource(R.drawable.profile_image)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //context는 parent에 있다
        return ViewHolder(ItemCommentBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<CommentModel>() {
            override fun areItemsTheSame(oldModel: CommentModel, newModel: CommentModel): Boolean {
                return oldModel.commentId == newModel.commentId
            }

            override fun areContentsTheSame(oldModel: CommentModel, newModel: CommentModel): Boolean {
                return oldModel == newModel
            }
        }
    }
}