package kr.ac.konkuk.koogle.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.ac.konkuk.koogle.Data.TagData
import kr.ac.konkuk.koogle.R

/*
    2021-05-27 주예진 작성
    프로필에서 표시되는 후기 목록 Recycler View 의 row adapter
    유저 프로필 이미지, 유저네임, 리뷰 내용을 표시
    현재 임시로 데이터를 문자열로 받아오게 구현함 수정 필요
 */
class RecommandAdapter(val context: Context, val items:ArrayList<ArrayList<String>>)
    :RecyclerView.Adapter<RecommandAdapter.ViewHolder>() {
    interface OnItemClickListener{
        fun OnItemClick(holder: TagAdapter.ViewHolder, view: View, data: TagData, position: Int)
    }
    var itemClickListener: OnItemClickListener?=null

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var recommandUserNameView: TextView = itemView.findViewById(R.id.recommandUserNameText)
        var recommandTextView: TextView = itemView.findViewById(R.id.recommandText)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommandAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_recommand, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecommandAdapter.ViewHolder, position: Int) {
        holder.recommandUserNameView.text = items[position][0]
        holder.recommandTextView.text = items[position][1]
    }

    override fun getItemCount(): Int {
        return items.size
    }
}