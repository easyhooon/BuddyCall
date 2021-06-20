package kr.ac.konkuk.koogle.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.ac.konkuk.koogle.Adapter.CommentAdapter
import kr.ac.konkuk.koogle.Adapter.TagAdapter
import kr.ac.konkuk.koogle.DBKeys
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_COMMENTS
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_USERS
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_USER_TAG
import kr.ac.konkuk.koogle.DBKeys.Companion.TAG_INDEX
import kr.ac.konkuk.koogle.Model.CommentModel
import kr.ac.konkuk.koogle.Model.TagModel
import kr.ac.konkuk.koogle.Model.UserModel
import kr.ac.konkuk.koogle.R
import kr.ac.konkuk.koogle.databinding.ActivityCheckProfileBinding

class CheckProfileActivity : AppCompatActivity() {
    private val maxShowTag = 20 // 최대 허용 태그 개수

    lateinit var binding: ActivityCheckProfileBinding

    lateinit var userTagRef: DatabaseReference

    lateinit var writerId: String

    private lateinit var commentAdapter: CommentAdapter

    private var userCommentList =  mutableListOf<CommentModel>()

    private val auth:FirebaseAuth by lazy {
        Firebase.auth
    }

    private val firebaseUser= auth.currentUser!!

    private val userRef: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_USERS)
    }

    private val currentWriterRef: DatabaseReference by lazy {
        userRef.child(writerId)
    }

    private val currentWriterCommentRef: DatabaseReference by lazy {
        currentWriterRef.child(DB_COMMENTS)
    }

    // 태그 리사이클러뷰 관련
    lateinit var tagAdapter: TagAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        initDB()
        initButton()
    }

    private fun initButton() {
        binding.apply {
            checkButton.setOnClickListener {
                finish()
            }
        }

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun initDB() {
        //파이어베이스 데이터베이스의 정보 가져오기
        intent.let{
            writerId = it.getStringExtra(ArticleActivity.WRITER_INFO).toString()
            Log.i("CheckProfileActivity", "writerId: $writerId")
        }

        // DB 에서 유저 태그 데이터 받아옴
        val tagData: ArrayList<TagModel> = arrayListOf()
        userTagRef = Firebase.database.reference
            .child(DB_USER_TAG).child(writerId)
        userTagRef.orderByChild(TAG_INDEX)
            .limitToFirst(maxShowTag).addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(s in snapshot.children){
                        val newSubTag = arrayListOf<String>()
                        for(st in s.child(DBKeys.SUB_TAGS).children){
                            newSubTag.add(st.key.toString())
                        }
                        tagData.add(
                            TagModel(
                                s.key.toString(), newSubTag,
                                s.child(DBKeys.TAG_VALUE).value.toString().toInt(),
                                s.child(DBKeys.TAG_TYPE).value.toString().toInt()
                            ))
                    }
                    // 로딩 작업이 끝난 이후 RecyclerView 를 초기화하는 순서를 맞추기 위해 이곳에 넣음
                    initTagRecyclerView(tagData)
                }
                override fun onCancelled(error: DatabaseError) {
                    return
                }
            })

        currentWriterRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userModel: UserModel? = snapshot.getValue(UserModel::class.java)
                    if (userModel != null) {
                        Log.i("CheckProfileActivity", "userModel: $userModel ")
                        if (userModel.userProfileImageUrl.isEmpty()) {
                            binding.apply {
                                userProfileImage.setImageResource(R.drawable.profile_image)
                            }
                        } else {
                            Glide.with(binding.userProfileImage)
                                .load(userModel.userProfileImageUrl)
                                .into(binding.userProfileImage)
                        }
                    }
                    if (userModel != null) {
                        binding.writerNameText.text = userModel.userName
                        binding.userNameText.text = userModel.userName
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        currentWriterCommentRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val commentModel = snapshot.getValue(CommentModel::class.java)
                    Log.i("CheckProfileActivity", "commentModel: $commentModel")
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

    private fun initCommentRecyclerView() {
        binding.commentRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.commentRecyclerView.addItemDecoration(DividerItemDecoration(this, 1))
        commentAdapter = CommentAdapter()
        binding.commentRecyclerView.adapter = commentAdapter
        commentAdapter.submitList(userCommentList)
    }

    private fun initTagRecyclerView(data: ArrayList<TagModel>) {
        binding.tagRecyclerView.layoutManager = LinearLayoutManager(this)
        // 구분선 넣기
        //binding.tagRecyclerView.addItemDecoration(DividerItemDecoration(this, 1))

        tagAdapter = TagAdapter(this, data, false)
        // 서브태그들 클릭했을 때 이벤트 구현
        tagAdapter.subTagClickListener = object : TagAdapter.OnItemClickListener {
            override fun onItemClick(
                holder: TagAdapter.DefaultViewHolder,
                view: EditText,
                data: TagModel,
                position: Int
            ) {
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