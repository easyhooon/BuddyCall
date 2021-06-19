package kr.ac.konkuk.koogle.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.ac.konkuk.koogle.Adapter.CommentAdapter
import kr.ac.konkuk.koogle.Adapter.TagAdapter
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_COMMENTS
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_USERS
import kr.ac.konkuk.koogle.Model.CommentModel
import kr.ac.konkuk.koogle.Model.GroupModel
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
    private val profileEditRequest = 1110
    // private var tag_debug_data: ArrayList<TagModel> = ArrayList()
    lateinit var binding: ActivityProfileBinding
    lateinit var commentAdapter: CommentAdapter
    private var userCommentList =  mutableListOf<CommentModel>()

    private val userRef: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_USERS)
        //val userRef = FirebaseDatabase.getInstance().getReference(DB_USERS).child(uid)와 같다
    }

    private val currentUserRef: DatabaseReference by lazy {
        userRef.child(firebaseUser.uid)
    }

    private val currentUserCommentRef: DatabaseReference by lazy {
        currentUserRef.child(DB_COMMENTS)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        initDB()
        initButton()
    }

    private fun initButton() {
        binding.accountInfoButton.setOnClickListener {
            val intent = Intent(this, AccountInfoActivity::class.java)
            startActivity(intent)
        }

        binding.profileEditButton.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivityForResult(intent, profileEditRequest)
        }

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun initDB() {
        //파이어베이스 데이터베이스의 정보 가져오기
        currentUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
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

        currentUserCommentRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val commentModel = snapshot.getValue(CommentModel::class.java)
                    Log.i("ProfileActivity", "commentModel: $commentModel")
                    if (commentModel != null) {
                        userCommentList.add(commentModel)
                    }

                }
                //동기적 실행을 위해 위치 옮김
                initCommentRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 리사이클러뷰 갱신
        if(requestCode==profileEditRequest){
            initRecyclerView()
        }

    }

    private fun initCommentRecyclerView() {
        binding.commentRecyclerView.layoutManager = LinearLayoutManager(this)
        // 구분선 넣기
        binding.commentRecyclerView.addItemDecoration(DividerItemDecoration(this, 1))
        commentAdapter = CommentAdapter()
        binding.commentRecyclerView.adapter = commentAdapter
        commentAdapter.submitList(userCommentList)
    }

    override fun initTagRecyclerView(data: ArrayList<TagModel>) {
        binding.tagRecyclerView.layoutManager = LinearLayoutManager(this)
        // 구분선 넣기
        //binding.tagRecyclerView.addItemDecoration(DividerItemDecoration(this, 1))

        tagAdapter = TagAdapter(this, data)
        tagAdapter.itemClickListener = object : TagAdapter.OnItemClickListener {
            override fun onItemClick(
                holder: TagAdapter.DefaultViewHolder,
                view: View,
                data: TagModel,
                position: Int
            ) {
                // 미구현
            }
        }
        binding.tagRecyclerView.adapter = tagAdapter
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
        itemTouchHelper.attachToRecyclerView(binding.tagRecyclerView)
    }
}