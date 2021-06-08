package kr.ac.konkuk.koogle.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kr.ac.konkuk.koogle.Adapter.RecommendAdapter
import kr.ac.konkuk.koogle.Adapter.TagAdapter
import kr.ac.konkuk.koogle.DBKeys
import kr.ac.konkuk.koogle.Model.TagModel
import kr.ac.konkuk.koogle.Model.UserModel
import kr.ac.konkuk.koogle.R
import kr.ac.konkuk.koogle.databinding.ActivityEditProfileBinding
import kr.ac.konkuk.koogle.databinding.ActivityProfileBinding

class ProfileEditActivity : AppCompatActivity() {
    private var tag_debug_data: ArrayList<TagModel> = ArrayList()
    private var recommend_debug_data: ArrayList<ArrayList<String>> = ArrayList()
    lateinit var binding: ActivityEditProfileBinding
    lateinit var tagRecyclerView: RecyclerView
    lateinit var tagAdapter: TagAdapter

    //파이어베이스 인증 객체 초기화
    private val auth = Firebase.auth

    //DB 객체 초기화
    private val firebaseUser = auth.currentUser!!
    private val storage = FirebaseStorage.getInstance()
//    private val storageRef = storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    fun init() {
        initData()
        initTagRecyclerView()
        initUserInfo()
        initButton()
    }

    private fun initButton() {
        binding.backButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.userNameChangeBtn.setOnClickListener {
            setActiveChangeUserName(true)
        }

        binding.userNameChangeUpdateBtn.setOnClickListener {
            setActiveChangeUserName(false)
        }

        binding.addNewTagBtn.setOnClickListener {
            val intent = Intent(this, AddNewTagActivity::class.java)
            startActivity(intent)
        }
    }

    // 유저 네임 변경 상태 활성화, 비활성화
    private fun setActiveChangeUserName(active: Boolean){
        val uid = firebaseUser.uid
        val currentUserRef = Firebase.database.reference.child(DBKeys.DB_USERS).child(uid)
        if(active){
            // 이름 입력 뷰 표시
            binding.userNameText.visibility = View.GONE
            binding.userNameEdit.visibility = View.VISIBLE
            // 닉네임 변경사항 저장 버튼으로 변경
            binding.userNameChangeUpdateBtn.visibility = View.VISIBLE
            binding.userNameChangeBtn.visibility = View.GONE
        }else{
            // 변경 사항 업데이트
            currentUserRef.child(DBKeys.USER_NAME).setValue(binding.userNameEdit.text.toString())

            // 이름 입력 뷰에서 표시 뷰로 변경
            binding.userNameText.visibility = View.VISIBLE
            binding.userNameEdit.visibility = View.GONE
            // 닉네임 변경 버튼으로 변경
            binding.userNameChangeUpdateBtn.visibility = View.GONE
            binding.userNameChangeBtn.visibility = View.VISIBLE
        }
    }


    private fun initUserInfo() {
        //입력 로그인용 유저의 데이터를 불러오기 위한 uid
        val uid = firebaseUser.uid
        val currentUserRef = Firebase.database.reference.child(DBKeys.DB_USERS).child(uid)
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
                        (binding.userNameEdit as TextView).text = userModel.userName
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
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

    }
}