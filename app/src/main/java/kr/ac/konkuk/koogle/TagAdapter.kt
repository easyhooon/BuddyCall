package kr.ac.konkuk.koogle

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class TagAdapter(val items:ArrayList<TagData>) : RecyclerView.Adapter<TagAdapter.ViewHolder>() {
    interface OnItemClickListener{
        fun OnItemClick(holder:ViewHolder, view:View, data:TagData, position: Int)
    }
    var itemClickListener:OnItemClickListener?=null

    fun moveItem(oldPos:Int, newPos:Int){
        val item = items[oldPos]
        items.removeAt(oldPos)
        items.add(newPos, item)
        notifyItemMoved(oldPos, newPos)
    }

    fun removeItem(pos:Int){
        items.removeAt(pos)
        notifyItemRemoved(pos)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var mainTagView: TextView = itemView.findViewById(R.id.mainTagText)
        var subTagView: TableLayout = itemView.findViewById<TableLayout>(R.id.subTagView)
        init{
            itemView.setOnClickListener{
                itemClickListener?.OnItemClick(this, it, items[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // row 를 실체화한다
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_user_tag, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mainTagView.text = items[position].main_tag_name
        // initSubTags(holder.subTagView, position)
    }
/*
    private fun initSubTags(table: TableLayout, position: Int){

        for (i in items[position].sub_tag_list) {
            val row = TableRow()
            val lp: TableRow.LayoutParams = LayoutParams(TableRow.LayoutParams.WRAP_CONTENT)
            row.setLayoutParams(lp)
            val itemList : ArrayList<String>
            var subTagText = TextView(Activity())
            subTagText.text = i
            row.addView(subTagText)


            ll.addView(row, i)
        }
    }
    private fun addTagItem(table: TableLayout, item: TextView){
        // 한 row가 가득 차면 새로운 row 생성
        // 조건 미구현
        if (false){
            table.addView(TableRow(this))
        }
    }
*/
    override fun getItemCount(): Int {
        return items.size
    }
}
