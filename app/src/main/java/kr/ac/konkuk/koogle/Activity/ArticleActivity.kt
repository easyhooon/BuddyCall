package kr.ac.konkuk.koogle.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.ac.konkuk.koogle.DBKeys.Companion.ARTICLE_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.CURRENT_NUMBER
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_ARTICLES
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_GROUPS
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_USERS
import kr.ac.konkuk.koogle.DBKeys.Companion.USER_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.USER_NAME
import kr.ac.konkuk.koogle.DBKeys.Companion.USER_PROFILE_IMAGE_URL
import kr.ac.konkuk.koogle.Model.ArticleModel
import kr.ac.konkuk.koogle.Model.Entity.SearchResultEntity
import kr.ac.konkuk.koogle.Model.UserModel
import kr.ac.konkuk.koogle.databinding.ActivityArticleBinding
import java.text.SimpleDateFormat
import java.util.*
import kr.ac.konkuk.koogle.R


class ArticleActivity : AppCompatActivity() {

    lateinit var binding: ActivityArticleBinding

    private lateinit var writerId:String
    private lateinit var articleId:String

    private lateinit var currentUserId:String
    private lateinit var currentUserName:String
    private lateinit var currentUserProfileImage:String

    private lateinit var recruitmentNumber:String
    private lateinit var currentNumber:String

    private var userIdList:MutableList<String> = mutableListOf()

    private lateinit var mapInfo:SearchResultEntity

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val currentUserRef: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_USERS).child(auth.currentUser?.uid.toString())
    }

    private val currentArticleRef: DatabaseReference by lazy{
        Firebase.database.reference.child(DB_ARTICLES).child(articleId)
    }

    private val currentGroupUserRef: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_GROUPS).child(articleId).child(DB_USERS)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDB()
        initViews()
        initButton()
    }

    private fun initViews() {
       setSupportActionBar(binding.articleToolbar)
        val actionBar = supportActionBar!!
        actionBar.apply{
            setDisplayShowCustomEnabled(true)
            setDisplayShowTitleEnabled(false) //기본 제목을 없애줌
            setDisplayHomeAsUpEnabled(true) // 자동으로 뒤로가기 버튼 만들어줌
        }
    }

    //todo 글의 수정, 삭제 메뉴 옵션 구현 해야 함
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.article_option_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.updateArticle -> {

            }
            R.id.deleteArticle -> {
                //dialog 한번 뿌리고 진짜 삭제
            }
            else -> {
                //뒤로가기
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initButton() {
        binding.contactButton.setOnClickListener {
            currentUserId = auth.currentUser!!.uid

            //글을 올린 사람이 나 인 경우
            if(writerId == currentUserId){
                Toast.makeText(this, "내가 작성한 글 입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //연락하여 이미 그룹에 포함되어있을 경우
            else if (currentUserId in userIdList){
                Toast.makeText(this, "이미 그룹에 가입하였습니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else{
                //글을 올린 사람이 내가 아닌 경우
                //채팅방 인원 + 1 명이 모집인원 보다 적을 경우
                if (currentNumber.toInt() + 1 <= recruitmentNumber.toInt())
                {
                    showProgress()
                    joinGroup(currentUserId, currentUserName, currentUserProfileImage)
                }
                else {
                    Toast.makeText(this, "모집인원을 초과하였습니다", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

            }
        }

        binding.showMapButton.setOnClickListener {
            val intent = Intent(this, CheckMapActivity::class.java)
            intent.putExtra(MAP_INFO, mapInfo)
            startActivity(intent)
        }
    }

    private fun initDB() {
        val intent = intent
        articleId = intent.getStringExtra(ARTICLE_ID).toString()
        //파이어베이스 데이터베이스의 정보 가져오기
        currentArticleRef.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SimpleDateFormat")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val articleModel: ArticleModel? = snapshot.getValue(ArticleModel::class.java)
                    val format = SimpleDateFormat("MM월 dd일")
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
                        //작성 글에 장소를 기입했을 경우
                        if (articleModel.desiredLocation != null){
                            mapInfo = articleModel.desiredLocation
                            binding.locationTextView.text = articleModel.desiredLocation.fullAddress
                            binding.writerNameTextView.text =articleModel.writerName
                            binding.titleTextView.text = articleModel.articleTitle
                            val date = Date(articleModel.articleCreatedAt)
                            binding.dateTextView.text = format.format(date).toString()
                            binding.contentTextView.text = articleModel.articleContent
                            recruitmentNumber = articleModel.recruitmentNumber.toString()
                            currentNumber = articleModel.currentNumber.toString()
                        }
                        else {
                            //장소를 기입하지 않았을 경우
                            //위치 관련 레이아웃이 아예 보이지 않게
                            binding.locationInfoLayout.visibility =  View.GONE
                            binding.writerNameTextView.text =articleModel.writerName
                            binding.titleTextView.text = articleModel.articleTitle
                            val date = Date(articleModel.articleCreatedAt)
                            binding.dateTextView.text = format.format(date).toString()
                            binding.contentTextView.text = articleModel.articleContent
                            recruitmentNumber = articleModel.recruitmentNumber.toString()
                            currentNumber = articleModel.currentNumber.toString()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ArticleActivity, "데이터 로드 실패", Toast.LENGTH_SHORT).show()
            }
        })

        currentGroupUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userIdList.clear()

                //그룹에 속한 유저들의 데이터를 가져옴
                for (snapshot in dataSnapshot.children) { //반복문을 통해 데이터 List를 추출해냄.
                    val userModel = snapshot.getValue(UserModel::class.java)

                    if (userModel != null) {
                        userIdList.add(userModel.userId)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

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

        currentGroupUserRef.setValue(user)
//        currentGroupUserRef.updateChildren(user)

        //그룹과 현 게시글에 가입 인원 갱신
        val currentGroupRef = Firebase.database.reference.child(DB_GROUPS).child(articleId)
        val group = mutableMapOf<String, Any>()
        group[CURRENT_NUMBER] = currentNumber.toInt() + 1
//        currentGroupRef.setValue(group)
        currentGroupRef.updateChildren(group)

        val currentArticleRef = Firebase.database.reference.child(DB_ARTICLES).child(articleId)
        val article = mutableMapOf<String, Any>()
        article[CURRENT_NUMBER] = currentNumber.toInt() + 1
//        currentGroupRef.setValue(group)
        currentArticleRef.updateChildren(article)

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

    companion object {
        const val MAP_INFO = "MAP_INFO"
    }
}