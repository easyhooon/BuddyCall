package kr.ac.konkuk.koogle

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.ac.konkuk.koogle.databinding.ActivityProfileBinding


class ProfileActivity : AppCompatActivity() {
    var data:ArrayList<TagData> = ArrayList()
    lateinit var binding : ActivityProfileBinding
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: TagAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }
    fun init(){
        initData()
        initRecyclerView()
    }
    private fun initRecyclerView() {
        recyclerView = findViewById<RecyclerView>(R.id.tagRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        // 구분선 넣기
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, 1))

        adapter = TagAdapter(data)
        adapter.itemClickListener = object : TagAdapter.OnItemClickListener{
            override fun OnItemClick(
                    holder: TagAdapter.ViewHolder,
                    view: View,
                    data: TagData,
                    position: Int
            ) {
                // 미구현
            }
        }
        recyclerView.adapter = adapter
        val simpleCallBack = object: ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN or ItemTouchHelper.UP,
                ItemTouchHelper.RIGHT){
            override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
            ): Boolean {
                adapter.moveItem(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.removeItem(viewHolder.adapterPosition)
            }

        }
        val itemTouchHelper = ItemTouchHelper(simpleCallBack)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
    private fun initData(){
        // 임시 데이터
        data.add(TagData("언어", arrayListOf("한국어", "영어")))
        data.add(TagData("성격", arrayListOf("활동적인", "솔직한")))
        data.add(TagData("취미", arrayListOf("영화감상", "게임", "서핑",
                "여행", "독서", "술", "요리", "그림그리기")))
        data.add(TagData("전공", arrayListOf("컴퓨터", "컴퓨터공학")))
        data.add(TagData("언어", arrayListOf("한국어", "영어")))
        data.add(TagData("성격", arrayListOf("활동적인", "솔직한")))
        data.add(TagData("취미", arrayListOf("영화감상", "게임", "서핑",
                "여행", "독서", "술", "요리", "그림그리기")))
        data.add(TagData("전공", arrayListOf("컴퓨터", "컴퓨터공학")))
        data.add(TagData("언어", arrayListOf("한국어", "영어")))
        data.add(TagData("성격", arrayListOf("활동적인", "솔직한")))
        data.add(TagData("취미", arrayListOf("영화감상", "게임", "서핑",
                "여행", "독서", "술", "요리", "그림그리기")))
        data.add(TagData("전공", arrayListOf("컴퓨터", "컴퓨터공학")))
    }
}