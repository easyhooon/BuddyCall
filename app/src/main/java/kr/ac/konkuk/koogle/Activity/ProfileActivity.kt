package kr.ac.konkuk.koogle.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kr.ac.konkuk.koogle.Adapter.RecommendAdapter
import kr.ac.konkuk.koogle.Adapter.TagAdapter
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_USERS
import kr.ac.konkuk.koogle.Model.TagModel
import kr.ac.konkuk.koogle.Model.UserModel
import kr.ac.konkuk.koogle.R
import kr.ac.konkuk.koogle.databinding.ActivityProfileBinding

/*
    2021-05-27 주예진 수정
    initData: 개발용 임시 데이터
    initTagRecyclerView: 태그 리스트 출력
    initRecommandRecyclerView: 타 유저의 추천(후기) 글 리스트
 */

//private var backBtnTime: Long = 0 // 뒤로가기 두번 눌러 종료 용 변수

class ProfileActivity : AppCompatActivity() {
    private var tag_debug_data: ArrayList<TagModel> = ArrayList()
    private var recommend_debug_data: ArrayList<ArrayList<String>> = ArrayList()
    lateinit var binding: ActivityProfileBinding
    lateinit var tagRecyclerView: RecyclerView
    lateinit var recommendRecyclerView: RecyclerView
    lateinit var tagAdapter: TagAdapter
    lateinit var recommendAdapter: RecommendAdapter

    //파이어베이스 인증 객체 초기화
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    //DB 객체 초기화
    private val firebaseUser = auth.currentUser!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    fun init() {
        initData()
        initTagRecyclerView()
        initRecommendRecyclerView()
        initUserInfo()
        initButton()
    }

    private fun initButton() {
        binding.accountInfoButton.setOnClickListener {
            val intent = Intent(this, AccountInfoActivity::class.java)
            startActivity(intent)
        }

        binding.profileEditButton.setOnClickListener {
            val intent = Intent(this, ProfileEditActivity::class.java)
            startActivity(intent)
        }

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }


    private fun initUserInfo() {
        //입력 로그인용 유저의 데이터를 불러오기 위한 uid
        val uid = firebaseUser.uid
        val currentUserRef = Firebase.database.reference.child(DB_USERS).child(uid)
//        val userRef = FirebaseDatabase.getInstance().getReference(DB_USERS).child(uid)와 같다

//        파이어베이스 데이터베이스의 정보 가져오기
        currentUserRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userModel: UserModel? = snapshot.getValue(UserModel::class.java)
                    if (userModel != null) {
                        if (userModel.userProfileImageUrl.isEmpty()) {
                            binding.userProfileImage.setImageResource(R.drawable.profile_image)
                        } else {
                            Glide.with(binding.userProfileImage)
                                .load(userModel.userProfileImageUrl)
                                .into(binding.userProfileImage)
                        }
                    }
                    if (userModel != null) {
                        binding.userNameText.text = userModel.userName
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun initRecommendRecyclerView() {
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
                data: TagModel,
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
        tag_debug_data.add(TagModel("언어", arrayListOf("한국어", "영어")))
        tag_debug_data.add(TagModel("성격", arrayListOf("활동적인", "솔직한")))
        tag_debug_data.add(
            TagModel(
                "취미", arrayListOf(
                    "영화감상", "게임", "서핑",
                    "여행", "독서", "술", "요리", "그림그리기"
                )
            )
        )
        tag_debug_data.add(TagModel("전공", arrayListOf("컴퓨터", "컴퓨터공학")))
        tag_debug_data.add(TagModel("언어", arrayListOf("한국어", "영어")))
        tag_debug_data.add(TagModel("성격", arrayListOf("활동적인", "솔직한")))
        tag_debug_data.add(
            TagModel(
                "해외여행", arrayListOf(
                    "러시아", "태국", "중국",
                    "싱가폴", "미국", "캐나다", "브라질", "그린란드", "영국", "대만"
                )
            )
        )
        tag_debug_data.add(TagModel("전공", arrayListOf("컴퓨터", "컴퓨터공학")))
        tag_debug_data.add(TagModel("언어", arrayListOf("한국어", "영어")))
        tag_debug_data.add(TagModel("성격", arrayListOf("활동적인", "솔직한")))
        tag_debug_data.add(
            TagModel(
                "취미", arrayListOf(
                    "영화감상", "게임", "서핑",
                    "여행", "독서", "술", "요리", "그림그리기"
                )
            )
        )
        tag_debug_data.add(TagModel("전공", arrayListOf("컴퓨터", "컴퓨터공학")))

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

//    //뒤로가기 두번 눌러 종료
//    override fun onBackPressed() {
//        val curTime = System.currentTimeMillis()
//        val gapTime: Long = curTime - backBtnTime
//
//        //뒤로가기를 한번 누른 후에 2초가 지나기전에 한번 더 눌렀을 경우 if문 진입
//        if (gapTime in 0..2000) {
//            super.onBackPressed()
//        } else {
//            backBtnTime = curTime
//            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
//        }
//    }
}