package kr.ac.konkuk.koogle.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import kr.ac.konkuk.koogle.Adapter.TagAdapter
import kr.ac.konkuk.koogle.DBKeys
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_USERS
import kr.ac.konkuk.koogle.DBKeys.Companion.USER_NAME
import kr.ac.konkuk.koogle.DBKeys.Companion.USER_PROFILE_IMAGE_PATH
import kr.ac.konkuk.koogle.DBKeys.Companion.USER_PROFILE_IMAGE_URL
import kr.ac.konkuk.koogle.Model.TagModel
import kr.ac.konkuk.koogle.Model.UserModel
import kr.ac.konkuk.koogle.R
import kr.ac.konkuk.koogle.databinding.ActivityEditProfileBinding
import java.util.*
import kotlin.collections.ArrayList


class EditProfileActivity : AppCompatActivity() {
    private var tag_debug_data: ArrayList<TagModel> = ArrayList()
    private var recommend_debug_data: ArrayList<ArrayList<String>> = ArrayList()
    lateinit var binding: ActivityEditProfileBinding
//    lateinit var tagRecyclerView: RecyclerView
    lateinit var tagAdapter: TagAdapter

    lateinit var imageUri: Uri

    lateinit var fileRef:StorageReference

    //파이어베이스 인증 객체 초기화
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    //DB 객체 초기화
    private val firebaseUser = auth.currentUser!!
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(auth.currentUser == null){
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }
        initData()
        initTagRecyclerView()
        initUserInfo()
        initButton()
    }

    private fun initButton() {
        binding.backButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
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
        binding.userProfileChangeBtn.setOnClickListener {
            //에뮬레이터에는 해당 저장소가 존재하지 않아 기능하지 않음, 실기기에 연결해서 수행해야함
            CropImage.activity()
                .setAspectRatio(1,1)
                .start(this);
        }
        binding.profileEditButton.setOnClickListener {
            finish()
        }
    }

    // 유저 네임 변경 상태 활성화, 비활성화
    private fun setActiveChangeUserName(active: Boolean){
        val uid = firebaseUser.uid
        val currentUserRef = Firebase.database.reference.child(DB_USERS).child(uid)
        if(active){
            // 이름 입력 뷰 표시
            binding.userNameText.visibility = View.GONE
            binding.userNameEdit.visibility = View.VISIBLE
            // 닉네임 변경사항 저장 버튼으로 변경
            binding.userNameChangeUpdateBtn.visibility = View.VISIBLE
            binding.userNameChangeBtn.visibility = View.GONE
        }else{
            // 변경 사항 업데이트
            currentUserRef.child(USER_NAME).setValue(binding.userNameEdit.text.toString())

            // 이름 입력 뷰에서 표시 뷰로 변경
            binding.userNameText.visibility = View.VISIBLE
            binding.userNameEdit.visibility = View.GONE
            // 닉네임 변경 버튼으로 변경
            binding.userNameChangeUpdateBtn.visibility = View.GONE
            binding.userNameChangeBtn.visibility = View.VISIBLE

            Toast.makeText(this, "닉네임을 변경하였습니다", Toast.LENGTH_SHORT).show()
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
                        (binding.userNameEdit as TextView).text = userModel.userName
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun initTagRecyclerView() {
        binding.tagRecyclerView.layoutManager = LinearLayoutManager(this)
        // 구분선 넣기
        binding.tagRecyclerView.addItemDecoration(DividerItemDecoration(this, 1))

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

    private fun initData() {
        // 임시 데이터
        tag_debug_data.add(TagModel("언어", arrayListOf("한국어", "영어"), -1, 0))
        tag_debug_data.add(TagModel("성격", arrayListOf("활동적인", "솔직한"), -1, 0))
        tag_debug_data.add(
            TagModel(
                "취미", arrayListOf(
                    "영화감상", "게임", "서핑",
                    "여행", "독서", "술", "요리", "그림그리기"
                ), -1, 0
            )
        )
        tag_debug_data.add(TagModel("전공", arrayListOf("컴퓨터", "컴퓨터공학"), -1, 0))
        tag_debug_data.add(TagModel("언어", arrayListOf("한국어", "영어"), -1, 0))
        tag_debug_data.add(TagModel("성격", arrayListOf("활동적인", "솔직한"), -1, 0))
        tag_debug_data.add(
            TagModel(
                "해외여행", arrayListOf(
                    "러시아", "태국", "중국",
                    "싱가폴", "미국", "캐나다", "브라질", "그린란드", "영국", "대만"
                ), -1 ,0
            )
        )
        tag_debug_data.add(TagModel("전공", arrayListOf("컴퓨터", "컴퓨터공학"), -1 ,0))
        tag_debug_data.add(TagModel("언어", arrayListOf("한국어", "영어"), -1 ,0))
        tag_debug_data.add(TagModel("성격", arrayListOf("활동적인", "솔직한"), -1 ,0))
        tag_debug_data.add(
            TagModel(
                "취미", arrayListOf(
                    "영화감상", "게임", "서핑",
                    "여행", "독서", "술", "요리", "그림그리기"
                ), -1 ,0
            )
        )
        tag_debug_data.add(TagModel("전공", arrayListOf("컴퓨터", "컴퓨터공학"), -1 ,0))

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                imageUri = result.uri
                binding.userProfileImage.setImageURI(imageUri)
                
                updateProfileImage(imageUri)
                
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "${result.error}", Toast.LENGTH_SHORT).show() 
            }
        }
    }

    private fun updateProfileImage(imageUri: Uri?) {
        if (imageUri == null) {
            Toast.makeText(this, "이미지가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        val uid = firebaseUser.uid

        fileRef = FirebaseStorage.getInstance().reference.child(USER_PROFILE_IMAGE_PATH).child("$uid.jpg")

        val uploadTask = fileRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                throw task.exception!!
            }

            // Continue with the task to get the download URL
            fileRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUrl = task.result
                val updatedProfileImageUrl = downloadUrl.toString()
                val currentUserRef = Firebase.database.reference.child(DB_USERS).child(uid)
//                val user = mutableMapOf<String, Any>()
//                user[USER_PROFILE_IMAGE_URL] = updatedProfileImageUrl
//                currentUserRef.updateChildren(user)

//                updateChildren으로 하면 화면이 깜빡거려서 UI상 보기 안좋아서 수정
                currentUserRef.child(USER_PROFILE_IMAGE_URL).setValue(updatedProfileImageUrl)

                Toast.makeText(this, "프로필 사진이 변경되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "프로필 사진 변경을 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}