package kr.ac.konkuk.koogle.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add_tag.*
import kr.ac.konkuk.koogle.Adapter.TagAdapter
import kr.ac.konkuk.koogle.DBKeys
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_USERS
import kr.ac.konkuk.koogle.DBKeys.Companion.SUB_TAGS
import kr.ac.konkuk.koogle.DBKeys.Companion.TAG_INDEX
import kr.ac.konkuk.koogle.DBKeys.Companion.TAG_TYPE
import kr.ac.konkuk.koogle.DBKeys.Companion.TAG_VALUE
import kr.ac.konkuk.koogle.DBKeys.Companion.USER_NAME
import kr.ac.konkuk.koogle.DBKeys.Companion.USER_PROFILE_IMAGE_PATH
import kr.ac.konkuk.koogle.DBKeys.Companion.USER_PROFILE_IMAGE_URL
import kr.ac.konkuk.koogle.Model.TagModel
import kr.ac.konkuk.koogle.Model.UserModel
import kr.ac.konkuk.koogle.R
import kr.ac.konkuk.koogle.databinding.ActivityEditProfileBinding
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class EditProfileActivity : ProfileCommonActivity() {
    private val newTagRequest = 1110
    lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUserInfo()
        initButton()
    }

    // 전체 Tag DB 에 변경사항 반영 ( 사용 횟수 증가)
    private fun pushDBTag(){
        var tagRef = Firebase.database.reference.child(DBKeys.DB_MAIN_TAGS)

        for ((key, value) in tagAdapter.data) {
            tagRef.child(key)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // DB 에 같은 값이 있음: 사용 회수 증가
                        if (snapshot.childrenCount > 0) {
                            // 사용 회수 의미적으로 증가(=감소)
                            tagRef.child(key).child(DBKeys.USED).setValue(
                                snapshot.child(DBKeys.USED).value.toString().toInt() - 1
                            )
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }

    // 현재 상태를 user DB에 저장
    // 기존의 데이터는 사라진다.
    private fun saveTag(){
        userTagRef = Firebase.database.reference
            .child(DBKeys.DB_USER_TAG).child(firebaseUser.uid)
        var j = tagAdapter.itemCount
        val tags = mutableMapOf<String, Any>()
        for(value in tagAdapter.data) {
            val newTag = mutableMapOf<String, Any>()
            val newSubTag = mutableMapOf<String, Any>()
            for ((i, s) in value.sub_tag_list.withIndex()) {
                val content = s.split(" ")
                // 만약 아무 내용 없는 서브 태그가 있으면 무시한다.
                if(content[0]==null || content[0]==""|| content[0]==" ")
                    continue
                newSubTag[content[0]] = i
            }
            newTag[TAG_INDEX] = j
            newTag[SUB_TAGS] = newSubTag
            newTag[TAG_TYPE] = value.tag_type
            newTag[TAG_VALUE] = value.value
            tags[value.main_tag_name] = newTag
            j++
        }
        userTagRef.setValue(tags)
    }

    private fun initButton() {
        binding.backButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.addNewTagBtn.setOnClickListener {
            val intent = Intent(this, AddNewTagActivity::class.java)
            startActivityForResult(intent, newTagRequest)
        }
        binding.apply{
            userNameChangeBtn.setOnClickListener {
                setActiveChangeUserName(true)
            }

            userNameChangeUpdateBtn.setOnClickListener {
                setActiveChangeUserName(false)
            }

            userProfileChangeBtn.setOnClickListener {
                //에뮬레이터에는 해당 저장소가 존재하지 않아 기능하지 않음, 실기기에 연결해서 수행해야함
                CropImage.activity()
                    .setAspectRatio(1,1)
                    .start(this@EditProfileActivity);
            }
            profileEditButton.setOnClickListener {
                pushDBTag()
                saveTag()
                finish()
            }
            accountInfoButton.setOnClickListener {
                finish()
            }
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
    // new Tag Activity 로부터 전달받은 데이타를 리사이클러뷰 어댑터로 전달
    // resultData: new Tag Activity 로부터 전해받은 Data
    private fun tossToAdpater(resultData: HashMap<String, TagModel>? = null){

        var newList:ArrayList<TagModel> = arrayListOf()
        for((key, value) in resultData!!){
            newList.add(value)
        }
        tagAdapter.updateData(newList)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 새 태그 추가 액티비티에서 전달받은 경우
        if (requestCode == newTagRequest){
            val data = data?.extras?.getSerializable("selectedTags")
            if(data!=null)
                tossToAdpater(data  as HashMap<String, TagModel>)
        }


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

        fileRef = storage.reference.child(USER_PROFILE_IMAGE_PATH).child("$uid.jpg")

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

    override fun initTagRecyclerView(data: ArrayList<TagModel>) {
        binding.tagRecyclerView.layoutManager = LinearLayoutManager(this)
        // 구분선 넣기
        binding.tagRecyclerView.addItemDecoration(DividerItemDecoration(this, 1))

        tagAdapter = TagAdapter(this, data, true)
        // 서브태그들 클릭했을 때 이벤트 구현
        /*
        tagAdapter.subTagClickListener = object : TagAdapter.OnItemClickListener {
            override fun onItemClick(
                holder: TagAdapter.DefaultViewHolder,
                view: EditText,
                data: TagModel,
                position: Int
            ) {
                Log.d("jan", "Click")
                view.isEnabled = true
            }
        }*/
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

    private fun initUserInfo() {
        //입력 로그인용 유저의 데이터를 불러오기 위한 uid
        val uid = firebaseUser.uid
        val currentUserRef = Firebase.database.reference.child(DB_USERS).child(uid)
//        val userRef = FirebaseDatabase.getInstance().getReference(DB_USERS).child(uid)와 같다

//        파이어베이스 데이터베이스의 정보 가져오기
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
                        (binding.userNameEdit as TextView).text = userModel.userName
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}