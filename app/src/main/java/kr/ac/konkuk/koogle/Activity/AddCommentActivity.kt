package kr.ac.konkuk.koogle.Activity

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kr.ac.konkuk.koogle.DBKeys
import kr.ac.konkuk.koogle.DBKeys.Companion.ADMIN_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.COMMENT_CONTENT
import kr.ac.konkuk.koogle.DBKeys.Companion.COMMENT_CREATED_AT
import kr.ac.konkuk.koogle.DBKeys.Companion.COMMENT_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_ARTICLES
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_COMMENTS
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_GROUPS
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_USERS
import kr.ac.konkuk.koogle.DBKeys.Companion.GROUP_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.WRITER_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.WRITER_NAME
import kr.ac.konkuk.koogle.DBKeys.Companion.WRITER_PROFILE_IMAGE_URL
import kr.ac.konkuk.koogle.Model.ArticleModel
import kr.ac.konkuk.koogle.Model.GroupModel
import kr.ac.konkuk.koogle.Model.UserModel
import kr.ac.konkuk.koogle.R
import kr.ac.konkuk.koogle.databinding.ActivityAddCommentBinding

class AddCommentActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddCommentBinding

    private lateinit var writerProfileImageUrl: String

    private lateinit var adminId: String

    private lateinit var groupId: String

    private lateinit var writerId: String

    private lateinit var commentId: String

    private lateinit var commentContent: String

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val firebaseUser = auth.currentUser!!

    private val userRef: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_USERS)
    }

    private val commentRef: DatabaseReference by lazy {
        userRef.child(adminId).child(DB_COMMENTS)
    }

    private val currentCommentRef: DatabaseReference by lazy {
        userRef.child(adminId).child(DB_COMMENTS).child(commentId)
    }

    private val currentUserRef: DatabaseReference by lazy {
        userRef.child(firebaseUser.uid)
    }

    private val currentGroupRef: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_GROUPS).child(groupId)
    }

    private lateinit var writerName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDB()
        initButton()
    }

    private fun initDB() {
        val intent = intent
        adminId = intent.getStringExtra(ADMIN_ID).toString()
        groupId = intent.getStringExtra(GROUP_ID).toString()

        currentUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userModel: UserModel? = snapshot.getValue(UserModel::class.java)
                if (userModel != null) {
                    writerName = userModel.userName
                    writerProfileImageUrl = userModel.userProfileImageUrl
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("onCancelled: ", "데이터로드 실패")
            }

        })

        currentGroupRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val groupModel: GroupModel? = snapshot.getValue(GroupModel::class.java)
                if (groupModel != null) {
                    binding.titleTextView.text = groupModel.articleTitle
                    binding.adminNameTextView.text = groupModel.adminName

                    if (groupModel.adminProfileImageUrl.isEmpty()) {
                        binding.adminProfileImageView.setImageResource(R.drawable.profile_image)
                    } else {
                        Glide.with(binding.adminProfileImageView)
                            .load(groupModel.adminProfileImageUrl)
                            .into(binding.adminProfileImageView)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("onCancelled: ", "데이터로드 실패")
            }

        })
    }

    private fun initButton() {
        binding.submitButton.setOnClickListener {
            commentId = commentRef.push().key.toString()
            commentContent = binding.contentEditText.text.toString()
            writerId = firebaseUser.uid

            if (commentContent.isEmpty()) {
                Toast.makeText(this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ad = AlertDialog.Builder(this)
            ad.setMessage("리뷰를 등록하시겠습니까? \n(무분별한 비난의 경우 관리자의 의해 삭제 조치될 수 있습니다.) ")
            ad.setPositiveButton(
                "취소"
            ) { dialog, _ ->
                Toast.makeText(this, "등록이 취소되었습니다", Toast.LENGTH_SHORT)
                    .show()
                dialog.dismiss()
            }
            ad.setNegativeButton(
                "등록"
            ) { dialog, _ ->
                Toast.makeText(this, "리뷰가 등록되었습니다", Toast.LENGTH_SHORT).show()
                showProgress()
                addComment()

                dialog.dismiss()
            }
            ad.show()


        }

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun addComment() {
        val comment = mutableMapOf<String, Any>()
        comment[COMMENT_ID] = commentId
        comment[WRITER_ID] = writerId
        comment[WRITER_NAME] = writerName
        comment[WRITER_PROFILE_IMAGE_URL] = writerProfileImageUrl
        comment[COMMENT_CREATED_AT] = System.currentTimeMillis()
        comment[COMMENT_CONTENT] = commentContent

        currentCommentRef.setValue(comment)

        hideProgress()
        finish()
    }

    private fun showProgress() {
        binding.progressBar.isVisible = true
    }

    private fun hideProgress() {
        binding.progressBar.isVisible = false
    }
}