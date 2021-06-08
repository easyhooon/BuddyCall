package kr.ac.konkuk.koogle.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.ac.konkuk.koogle.DBKeys
import kr.ac.konkuk.koogle.DBKeys.Companion.ARTICLE_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_ARTICLES
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_GROUPS
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_USERS
import kr.ac.konkuk.koogle.DBKeys.Companion.USER_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.USER_NAME
import kr.ac.konkuk.koogle.DBKeys.Companion.USER_PROFILE_IMAGE_URL
import kr.ac.konkuk.koogle.Model.ArticleModel
import kr.ac.konkuk.koogle.Model.UserModel
import kr.ac.konkuk.koogle.R
import kr.ac.konkuk.koogle.databinding.ActivityArticleBinding

class ArticleActivity : AppCompatActivity() {

    lateinit var binding: ActivityArticleBinding

    lateinit var currentArticleRef: DatabaseReference
    lateinit var currentGroupRef:DatabaseReference


    private lateinit var writerId:String
    private lateinit var articleId:String

    private lateinit var currentUserId:String
    private lateinit var currentUserName:String
    private lateinit var currentUserProfileImage:String

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDB()
        initButton()
    }

    private fun initButton() {
        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.contactButton.setOnClickListener {
            currentUserId = auth.currentUser!!.uid

            //todo 이미 그룹에 포함된 사람도 예외처리 필요

            //글을 올린 사람이 나 인 경우
            if(writerId == currentUserId){
                Toast.makeText(this, "내가 작성한 글 입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else{
                //글을 올린 사람이 내가 아닌 경우
                //채팅방에 참여자로 등록
                showProgress()
                joinGroup(currentUserId, currentUserName, currentUserProfileImage)
            }
        }
    }

    private fun initDB() {
        val intent = intent
        articleId = intent.getStringExtra(ARTICLE_ID).toString()

        currentArticleRef = Firebase.database.reference.child(DB_ARTICLES).child(articleId)

        currentGroupRef = Firebase.database.reference.child(DB_GROUPS).child(articleId)

        //파이어베이스 데이터베이스의 정보 가져오기
        currentArticleRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val articleModel: ArticleModel? = snapshot.getValue(ArticleModel::class.java)
                    if (articleModel != null) {
                        writerId = articleModel.writerId
                        if (articleModel.articleImageUrl.isEmpty()) {
                            binding.photoImageView.visibility = View.GONE
                        } else {
                            Glide.with(binding.photoImageView)
                                .load(articleModel.articleImageUrl)
                                .into(binding.photoImageView)
                        }
                    }

                    if (articleModel != null) {
                        if (articleModel.writerProfileImageUrl.isEmpty()) {
                            binding.profileImageView.setImageResource(R.drawable.profile_image)
                        } else {
                            Glide.with(binding.profileImageView)
                                .load(articleModel.writerProfileImageUrl)
                                .into(binding.profileImageView)
                        }
                    }
                    if (articleModel != null) {
                        binding.titleTextView.text = articleModel.articleTitle
                        binding.contentTextView.text = articleModel.articleContent
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ArticleActivity, "데이터 로드 실패", Toast.LENGTH_SHORT).show()
            }
        })

        val currentUserRef = Firebase.database.reference.child(DB_USERS).child(auth.currentUser?.uid.toString())
        currentUserRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val userModel: UserModel? = snapshot.getValue(UserModel::class.java)
                if (userModel != null) {
                    Log.d("onDataChange", "userName: ${userModel.userName}")
                    currentUserName = userModel.userName
                    currentUserProfileImage = userModel.userProfileImageUrl
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("onCancelled: ", "데이터로드 실패")
            }
        })
    }

    private fun joinGroup(userId: String, userName: String, userProfileImage: String) {

        val currentGroupUserRef = Firebase.database.reference.child(DB_GROUPS).child(articleId).child(DB_USERS).child(currentUserId)
        val user = mutableMapOf<String, Any>()
        user[USER_ID] = userId
        user[USER_NAME] = userName
        user[USER_PROFILE_IMAGE_URL] = userProfileImage

        currentGroupUserRef.updateChildren(user)

        hideProgress()

        Toast.makeText(this, "채팅방이 생성되었습니다. 그룹을 확인해주세요.", Toast.LENGTH_SHORT).show()

        finish()
    }

    private fun showProgress() {
        binding.progressBar.isVisible = true
    }

    private fun hideProgress() {
        binding.progressBar.isVisible = false
    }
}