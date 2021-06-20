package kr.ac.konkuk.koogle.Adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.setMargins
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.ac.konkuk.koogle.DBKeys
import kr.ac.konkuk.koogle.Model.TagModel
import kr.ac.konkuk.koogle.Model.TagType
import kr.ac.konkuk.koogle.R

/*
    2021-06-18 주예진 작성
    프로필 편집에서 갈 수 있는 태그 추가 액티비티에서 Recycler View 의 row adapter

    문제가 일어날 수 있는 부분:
    selected 정보가 ViewHolder 에 저장되어있어서,
    selected 된 tag 들이 recyclerview 에서 새로 binding 되면서 사라지고,
    selectedList 는 그대로 남아있어서 오류가 발생할 수 있다.

    selected
 */
class AddTagAdapter(open val context: Context, open var data: MutableList<TagModel>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    var selectedList: HashMap<String, TagModel> = hashMapOf()

    // name: sub tag
    // selected: 해당 sub tag를 유저가 선택하였는지 여부
    data class subTagModel(var name:String, var selected:Boolean, var view:TextView)

    // Tag 밑에 여러 Tag 가 있는 형태의 row
    // Sub Tag 관련 함수는 TagAdapter.kt 의 ViewHolder class 와 중복되는 부분 나중에 통합 방법 생각해보기
    inner class DefaultViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val mainTagText: TextView = itemView.findViewById(R.id.mainTagText)
        var mainTag: String = ""
        val subTagView: LinearLayout = itemView.findViewById(R.id.subTagView)
        val TagRef: DatabaseReference = Firebase.database.reference.child(DBKeys.DB_MAIN_TAGS)
        var subTagList: ArrayList<subTagModel> = arrayListOf()

        // 유저가 검색창에 입력함에 따라 데이터가 변경됨
        fun setData(newData: MutableList<TagModel>){
            data = newData
            notifyDataSetChanged()
        }

        // SubTag 한 칸을 생성한다.
        fun makeSubTagView(tagName: String): TextView{
            var subTagText = TextView(context)
            subTagText.text = tagName
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

            // 서브 태그 클릭 이벤트 설정
            subTagText.setOnClickListener {
                // 1. 선택 여부 반전
                var i = 0
                for(s in subTagList){
                    if (s.name == tagName){
                        subTagList[i].selected = !subTagList[i].selected
                        // 2-1. 선택하기
                        if(subTagList[i].selected){
                            // 이미 리스트에 있으면 서브 태그 리스트에만 추가, 없으면 태그 모델 만들기
                            if(selectedList[mainTag]!=null){
                                selectedList[mainTag]!!.sub_tag_list.add(tagName)
                            }else{
                                selectedList[mainTag] =
                                    TagModel(mainTag, arrayListOf(tagName), 0, TagType.TAG)
                            }
                        }
                        // 2-2. 선택 해제
                        else{
                            selectedList[mainTag]!!.sub_tag_list.remove(tagName)
                            // 만약 모든 서브 태그 선택 해제했으면 태그도 제거
                            if(selectedList[mainTag]!!.sub_tag_list==null)
                                selectedList.remove(mainTag)
                        }
                        break
                    }
                    i++
                }
                // 3. 이미지 변경
                subTagRedraw(i)
            }

            return subTagText
        }
        // SubTag 를 다시 그린다.
        // SubTag 선택 여부가 바뀌었을 때 호출되어야 함
        private fun subTagRedraw(index: Int){
            // Log.d("jan", "subTagRedraw "+ subTagList[index].selected.toString())
            if(subTagList[index].selected){
                // 모서리가 둥근 태그 스타일 2 적용(임시)
                subTagList[index].view.setTextAppearance(R.style.TAG_STYLE_SELECTED)
                subTagList[index].view.setBackgroundResource(R.drawable.layout_tag_selected_background)
            }else{
                // 모서리가 둥근 태그 스타일 적용(임시)
                subTagList[index].view.setTextAppearance(R.style.TAG_STYLE)
                subTagList[index].view.setBackgroundResource(R.drawable.layout_tag_background)
            }
        }

        // 소분류 태그 테이블 생성
        fun bind() {
            for (tag in subTagList) {
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
                len += tag.name.length
                if (len > row_len) {
                    addRow()
                }
                lastRow =
                    subTagView.getChildAt(subTagView.childCount - 1) as LinearLayout

                val newView: TextView = makeSubTagView(tag.name)
                lastRow.addView(newView)
                tag.view = newView
            }
        }

        // Tag 이름으로 SubTag 테이블에서 인기있는 태그를 N개 질의해온다.
        fun settingSubTags(tagName: String, num: Int){
            TagRef.child(tagName).child(DBKeys.SUB_TAGS).orderByChild(DBKeys.USED).limitToFirst(num)
                .addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(s in snapshot.children){
                            subTagList.add(subTagModel(
                                s.key.toString(), false, TextView(context))
                            )
                        }
                        // SubTag List 에서 전부 받아 온 뒤에 bind 해준다
                        // 이렇게 해주는 이유는 순서를 반드시 맞추기 위해서입니다
                        bind()
                    }

                    override fun onCancelled(error: DatabaseError) {
                       return
                    }
                })
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
    }

    // Tag 밑에 수치형 데이터 가 있는 형태의 row
    inner class ValueViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val mainTagText: TextView = itemView.findViewById(R.id.mainTagText)
        val valueText: EditText = itemView.findViewById(R.id.tagValueText)
        var mainTag: String = ""
        init {
            valueText.addTextChangedListener(object: TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
                override fun afterTextChanged(s: Editable?) {
                    // 선택 리스트에 추가하기
                    if(valueText.length() != 0){
                        // 이미 리스트에 있으면 값만 변경, 없으면 태그 모델 만들기
                        if(selectedList[mainTag]!=null){
                            selectedList[mainTag]!!.value = valueText.text.toString().toInt()
                        }else{
                            selectedList[mainTag] =
                                TagModel(mainTag, arrayListOf(),
                                    valueText.text.toString().toInt(), TagType.VALUE)
                        }
                    }
                    // 선택 리스트에서 제거하기
                    else selectedList.remove(mainTag)
                }
            })
        }
    }

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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val tagName: String = data[position]!!.main_tag_name
        if(holder is DefaultViewHolder){
            holder.mainTag = tagName
            holder.mainTagText.text = tagName
            holder.settingSubTags(tagName, 20)
        }else if(holder is ValueViewHolder){
            holder.mainTag = tagName
            holder.mainTagText.text = tagName
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