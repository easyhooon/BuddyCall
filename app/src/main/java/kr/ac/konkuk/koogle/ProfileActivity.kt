package kr.ac.konkuk.koogle

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.ac.konkuk.koogle.Adapter.RecommendAdapter
import kr.ac.konkuk.koogle.Adapter.TagAdapter
import kr.ac.konkuk.koogle.Data.TagData
import kr.ac.konkuk.koogle.databinding.ActivityProfileBinding

/*
    2021-05-27 주예진 수정
    initData: 개발용 임시 데이터
    initTagRecyclerView: 태그 리스트 출력
    initRecommandRecyclerView: 타 유저의 추천(후기) 글 리스트
 */
class ProfileActivity : AppCompatActivity() {
    private var tag_debug_data: ArrayList<TagData> = ArrayList()
    private var recommend_debug_data: ArrayList<ArrayList<String>> = ArrayList()
    lateinit var binding: ActivityProfileBinding
    lateinit var tagRecyclerView: RecyclerView
    lateinit var recommendRecyclerView: RecyclerView
    lateinit var tagAdapter: TagAdapter
    lateinit var recommendAdapter: RecommendAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    fun init() {
        initData()
        initTagRecyclerView()
        initRecommandRecyclerView()
    }

    private fun initRecommandRecyclerView() {
        recommendRecyclerView = findViewById(R.id.recommendRecyclerView)
        recommendRecyclerView.layoutManager = LinearLayoutManager(this)
        // 구분선 넣기
        recommendRecyclerView.addItemDecoration(DividerItemDecoration(tagRecyclerView.context, 1))
        recommendAdapter = RecommendAdapter(this, recommend_debug_data)
        // 아이템 클릭 리스터 설정(미구현)
        recommendRecyclerView.adapter = recommendAdapter
    }

    private fun initTagRecyclerView() {
        tagRecyclerView = findViewById<RecyclerView>(R.id.tagRecyclerView)
        tagRecyclerView.layoutManager = LinearLayoutManager(this)
        // 구분선 넣기
        tagRecyclerView.addItemDecoration(DividerItemDecoration(tagRecyclerView.context, 1))

        tagAdapter = TagAdapter(this, tag_debug_data)
        tagAdapter.itemClickListener = object : TagAdapter.OnItemClickListener {
            override fun onItemClick(
                holder: TagAdapter.ViewHolder,
                view: View,
                data: TagData,
                position: Int
            ) {
                // 미구현
            }
        }
        tagRecyclerView.adapter = tagAdapter
        val simpleCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.DOWN or ItemTouchHelper.UP,
            ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                tagAdapter.moveItem(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                tagAdapter.removeItem(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleCallBack)
        itemTouchHelper.attachToRecyclerView(tagRecyclerView)
    }

    private fun initData() {
        // 임시 데이터
        tag_debug_data.add(TagData("언어", arrayListOf("한국어", "영어")))
        tag_debug_data.add(TagData("성격", arrayListOf("활동적인", "솔직한")))
        tag_debug_data.add(
            TagData(
                "취미", arrayListOf(
                    "영화감상", "게임", "서핑",
                    "여행", "독서", "술", "요리", "그림그리기"
                )
            )
        )
        tag_debug_data.add(TagData("전공", arrayListOf("컴퓨터", "컴퓨터공학")))
        tag_debug_data.add(TagData("언어", arrayListOf("한국어", "영어")))
        tag_debug_data.add(TagData("성격", arrayListOf("활동적인", "솔직한")))
        tag_debug_data.add(
            TagData(
                "해외여행", arrayListOf(
                    "러시아", "태국", "중국",
                    "싱가폴", "미국", "캐나다", "브라질", "그린란드", "영국", "대만"
                )
            )
        )
        tag_debug_data.add(TagData("전공", arrayListOf("컴퓨터", "컴퓨터공학")))
        tag_debug_data.add(TagData("언어", arrayListOf("한국어", "영어")))
        tag_debug_data.add(TagData("성격", arrayListOf("활동적인", "솔직한")))
        tag_debug_data.add(
            TagData(
                "취미", arrayListOf(
                    "영화감상", "게임", "서핑",
                    "여행", "독서", "술", "요리", "그림그리기"
                )
            )
        )
        tag_debug_data.add(TagData("전공", arrayListOf("컴퓨터", "컴퓨터공학")))

        recommend_debug_data.add(
            arrayListOf(
                "닉네임1", "ㅇㅇㅇ 교환 했었는데 친절하셨습니다 기분좋게" +
                        "거래했네요! 어쩌고 저쩌고~"
            )
        )
        recommend_debug_data.add(
            arrayListOf(
                "그린조아", "갑자기 약속 취소하고 잠수타셔서" +
                        " 시간만 낭비했네요"
            )
        )
    }
}