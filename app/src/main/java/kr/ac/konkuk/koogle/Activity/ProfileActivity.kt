package kr.ac.konkuk.koogle.Activity

import android.content.Intent
import android.os.Bundle
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
import kr.ac.konkuk.koogle.Adapter.CommentAdapter
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

class ProfileActivity : ProfileCommonActivity() {
    // private var tag_debug_data: ArrayList<TagModel> = ArrayList()
    lateinit var binding: ActivityProfileBinding
    lateinit var tagRecyclerView: RecyclerView
    lateinit var recommendRecyclerView: RecyclerView
    lateinit var tagAdapter: TagAdapter
    lateinit var commentAdapter: CommentAdapter


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

    private fun init() {
        initData()
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
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
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
        commentAdapter = CommentAdapter()
        // 아이템 클릭 리스터 설정(미구현)
        recommendRecyclerView.adapter = commentAdapter
    }

    override fun initTagRecyclerView(data: ArrayList<TagModel>) {
        tagRecyclerView = findViewById<RecyclerView>(R.id.tagRecyclerView)
        tagRecyclerView.layoutManager = LinearLayoutManager(this)
        // 구분선 넣기
        tagRecyclerView.addItemDecoration(DividerItemDecoration(tagRecyclerView.context, 1))

        tagAdapter = TagAdapter(this, data)
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

    private fun initData() {}
}