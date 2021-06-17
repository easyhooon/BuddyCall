package kr.ac.konkuk.koogle.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import kr.ac.konkuk.koogle.Model.TagModel
import kr.ac.konkuk.koogle.Model.TagType
import kr.ac.konkuk.koogle.R
import org.w3c.dom.Text


/*
    2021-06-18 주예진 작성
    프로필 편집에서 갈 수 있는 태그 추가 액티비티에서 Recycler View 의 row adapter
 */
class AddTagAdapter(val context: Context, val data: MutableList<TagModel?>)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View

        return when (viewType) {
            TagType.TAG ->{
                view = LayoutInflater.from(context).inflate(R.layout.row_add_tag, parent, false)
                DefaultViewHolder(view)
            }
            TagType.VALUE->{
                view = LayoutInflater.from(context).inflate(R.layout.row_add_tag_value, parent, false)
                ValueViewHolder(view)
            }
            else -> {
                view = LayoutInflater.from(context).inflate(R.layout.row_add_tag, parent, false)
                DefaultViewHolder(view)
            }
        }
    }

    // Tag 밑에 여러 Tag 가 있는 형태의 row
    inner class DefaultViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val mainTagText: TextView = itemView.findViewById(R.id.mainTagText)
        init {
        }
    }

    // Tag 밑에 수치형 데이터 가 있는 형태의 row
    inner class ValueViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val mainTagText: TextView = itemView.findViewById(R.id.mainTagText)
        init {
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is DefaultViewHolder){
            Log.d("jan", "${data[position]!!.main_tag_name}")
            holder.mainTagText.text = data[position]!!.main_tag_name
        }else if(holder is ValueViewHolder){
            holder.mainTagText.text = data[position]!!.main_tag_name
        }else {
            //(holder as SettingViewHolder)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    // 헤더의 경우 메뉴에 포함되지 않으므로 제외
    override fun getItemViewType(position: Int): Int {
        return data[position]!!.tag_type
    }

}