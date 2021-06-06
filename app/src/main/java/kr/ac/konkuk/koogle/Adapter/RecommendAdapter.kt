package kr.ac.konkuk.koogle.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.ac.konkuk.koogle.Model.TagModel
import kr.ac.konkuk.koogle.R

/*
    2021-05-27 주예진 작성
    프로필에서 표시되는 후기 목록 Recycler View 의 row adapter
    유저 프로필 이미지, 유저네임, 리뷰 내용을 표시
    현재 임시로 데이터를 문자열로 받아오게 구현함 수정 필요
 */
class RecommendAdapter(val context: Context, val items: ArrayList<ArrayList<String>>) :
    RecyclerView.Adapter<RecommendAdapter.ViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(holder: TagAdapter.ViewHolder, view: View, data: TagModel, position: Int)
    }

    var itemClickListener: OnItemClickListener? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var recommendUserNameView: TextView = itemView.findViewById(R.id.recommendUserNameText)
        var recommendTextView: TextView = itemView.findViewById(R.id.recommendText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_recommend, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecommendAdapter.ViewHolder, position: Int) {
        holder.recommendUserNameView.text = items[position][0]
        holder.recommendTextView.text = items[position][1]
    }

    override fun getItemCount(): Int {
        return items.size
    }
}