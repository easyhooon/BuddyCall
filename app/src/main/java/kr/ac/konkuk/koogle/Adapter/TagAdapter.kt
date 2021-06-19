package kr.ac.konkuk.koogle.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.children
import androidx.core.view.setMargins
import androidx.recyclerview.widget.RecyclerView
import kr.ac.konkuk.koogle.R
import kr.ac.konkuk.koogle.Model.TagModel

/*
    2021-05-27 주예진 작성
    프로필에서 표시되는 태그 Recycler View 의 row adapter
    대분류 태그(제목)와 소분류 태그를 표시
 */
class TagAdapter(val context: Context, val data: ArrayList<TagModel>) :
    RecyclerView.Adapter<TagAdapter.ViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(holder: ViewHolder, view: View, data: TagModel, position: Int)
    }
    var itemClickListener: OnItemClickListener? = null

    fun moveItem(oldPos: Int, newPos: Int) {
        val item = data[oldPos]
        data.removeAt(oldPos)
        data.add(newPos, item)
        notifyItemMoved(oldPos, newPos)
    }

    fun removeItem(pos: Int) {
        data.removeAt(pos)
        notifyItemRemoved(pos)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var mainTagView: TextView = itemView.findViewById(R.id.mainTagText)
        private var subTagView: LinearLayout = itemView.findViewById(R.id.subTagView)

        init {
            itemView.setOnClickListener {
                itemClickListener?.onItemClick(this, it, data[adapterPosition], adapterPosition)
            }
        }

        // 소분류 태그 테이블 생성
        fun bind(model: TagModel) {
            mainTagView.text = model.main_tag_name
            for (tag in model.sub_tag_list) {
                // row 가 하나도 없으면 새로 만들기
                if (subTagView.childCount == 0) {
                    addRow()
                }
                // 새로운 Table row 를 추가해야 하는지 길이 검사(임시)
                var lastRow: LinearLayout =
                    subTagView.getChildAt(subTagView.childCount - 1) as LinearLayout
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
                lastRow =
                    subTagView.getChildAt(subTagView.childCount - 1) as LinearLayout
                var subTagText = TextView(context)
                subTagText.text = tag
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
                lastRow.addView(subTagText)
            }
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
            subTagView.addView(row)
        }

        private fun addTagItem(table: TableLayout, item: TextView) {
            // 한 row가 가득 차면 새로운 row 생성
            // 조건 미구현
            if (false) {
                table.addView(TableRow(context))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_user_tag, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
