package kr.ac.konkuk.koogle.Activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.content.ContextCompat
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kr.ac.konkuk.koogle.DBKeys.Companion.ADMIN_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.ADMIN_NAME
import kr.ac.konkuk.koogle.DBKeys.Companion.ADMIN_PROFILE_IMAGE_URL
import kr.ac.konkuk.koogle.DBKeys.Companion.ARTICLE_CONTENT
import kr.ac.konkuk.koogle.DBKeys.Companion.ARTICLE_CREATED_AT
import kr.ac.konkuk.koogle.DBKeys.Companion.ARTICLE_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.ARTICLE_IMAGE_PATH
import kr.ac.konkuk.koogle.DBKeys.Companion.ARTICLE_IMAGE_URL
import kr.ac.konkuk.koogle.DBKeys.Companion.ARTICLE_TITLE
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_ARTICLES
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_GROUPS
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_USERS
import kr.ac.konkuk.koogle.DBKeys.Companion.GROUP_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.USER_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.USER_NAME
import kr.ac.konkuk.koogle.DBKeys.Companion.USER_PROFILE_IMAGE_URL
import kr.ac.konkuk.koogle.DBKeys.Companion.WRITER_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.WRITER_NAME
import kr.ac.konkuk.koogle.DBKeys.Companion.WRITER_PROFILE_IMAGE_URL
import kr.ac.konkuk.koogle.Model.Entity.SearchResultEntity
import kr.ac.konkuk.koogle.Model.UserModel
import kr.ac.konkuk.koogle.databinding.ActivityAddArticleBinding

class AddArticleActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddArticleBinding

    lateinit var writerProfileImageUrl: String

    lateinit var articleId: String

    private lateinit var searchResult: SearchResultEntity

    private var selectedUri: Uri? = null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }
    private val articleRef: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_ARTICLES)
    }
    private val groupRef: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_GROUPS)
    }
    private val userRef: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_USERS)
    }

    private lateinit var writerName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUserRef = userRef.child(auth.currentUser?.uid.toString())
        currentUserRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val userModel: UserModel? = snapshot.getValue(UserModel::class.java)
                if (userModel != null) {
                    Log.d("onDataChange", "userName: ${userModel.userName}")
                    writerName = userModel.userName
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("onCancelled: ", "데이터로드 실패")
            }

        })

        initButton()

    }

    private fun initButton() {
        binding.imageAddButton.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    //Permission 이 허용이 됬을 경우, ContentProvider 로 넘어감
                    startContentProvider()
                }
                //교육용 팝업이 필요한 경우
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    showPermissionContextPopup()
                }
                else -> {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1010
                    )
                }
            }
        }

        binding.submitButton.setOnClickListener {
            articleId = articleRef.push().key.toString()
            val articleTitle = binding.titleEditText.text.toString()
            val articleContent = binding.contentEditText.text.toString()
            val writerId = auth.currentUser?.uid.orEmpty()

            if (auth.currentUser?.photoUrl != null) {
                writerProfileImageUrl = auth.currentUser?.photoUrl.toString()
            }
            else {
                writerProfileImageUrl = ""
            }

            showProgress()


            //중간에 이미지가 있으면 업로드 과정을 추가
            if (selectedUri != null) {
                val photoUri = selectedUri ?: return@setOnClickListener
                uploadPhoto(photoUri,
                    //내부 비동기
                    successHandler = { uri ->
                        //이미지 uri를 첨부해서 업로드
                        //이미지가 있는 상황
                        //업로드한 url을 가져와서 같이 넣어줬기 때문에 url도 함께 들어갈 수 있음
                        uploadArticle(
                            writerId,
                            writerName,
                            writerProfileImageUrl,
                            articleId,
                            articleTitle,
                            articleContent,
                            uri
                        )
                        createChatRoom(
                            writerId,
                            writerName,
                            writerProfileImageUrl,
                            articleTitle,
                            articleContent,
                        )
                    },
                    errorHandler = {
                        //작업을 취소
                        Toast.makeText(this, "사진 업로드에 실패했습니다", Toast.LENGTH_SHORT).show()
                        hideProgress()
                    }
                )
            } else {
                //동기
                //이미지가 없는 상황
                uploadArticle(
                    writerId,
                    writerName,
                    writerProfileImageUrl,
                    articleId,
                    articleTitle,
                    articleContent,
                    ""
                )
                createChatRoom(
                    writerId,
                    writerName,
                    writerProfileImageUrl,
                    articleTitle,
                    articleContent,
                )
            }
        }

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //todo 기능 구현중
        binding.locationAddButton.setOnClickListener {
            val intent = Intent(this, LocationSearchActivity::class.java)
            startActivityForResult(intent, LOCATION_SEARCH_REQUEST_CODE)
        }
    }

    private fun createChatRoom(
        adminId: String,
        adminName: String,
        adminProfileImageUrl: String,
        articleTitle: String,
        articleContent: String)
    {
        val currentGroupRef = groupRef.child(articleId)
        val group = mutableMapOf<String, Any>()

        group[GROUP_ID] = articleId
        group[ARTICLE_TITLE] = articleTitle
        group[ARTICLE_CONTENT] = articleContent
        group[ADMIN_ID] = adminId
        group[ADMIN_NAME] = adminName
        group[ADMIN_PROFILE_IMAGE_URL] = adminProfileImageUrl

        currentGroupRef.updateChildren(group)

        //채팅방 생성 후에 방장(admin)이 채팅방에 참여자로 등록

        //채팅방은 유저ref와 메세지 ref로 구성
        val currentGroupUserRef = currentGroupRef.child(DB_USERS).child(adminId)
        val user = mutableMapOf<String, Any>()
        user[USER_ID] = adminId
        user[USER_NAME] = adminName
        user[USER_PROFILE_IMAGE_URL] = adminProfileImageUrl

        currentGroupUserRef.updateChildren(user)

        hideProgress()
        finish()

    }

    //successHandler의 반환값 String
    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
        //파일명이 중복이 되지 않도록
        //이렇게 두면 이거 삭제는 어떻게 하남
        val fileName = "${articleId}.png"
        storage.reference.child(ARTICLE_IMAGE_PATH).child(fileName)
            .putFile(uri)
            //성공했는지 여부를 체크
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    storage.reference.child(ARTICLE_IMAGE_PATH).child(fileName).downloadUrl
                        .addOnSuccessListener { uri ->
                            //업로드를 성공하면 download url을 가져옴
                            successHandler(uri.toString())
                        }.addOnFailureListener {
                            errorHandler()
                        }
                } else {
                    errorHandler()
                }
            }
    }

    private fun uploadArticle(
        writerId: String,
        writerName: String,
        writerProfileImageUrl: String,
        articleId: String,
        articleTitle: String,
        articleContent: String,
        articleImageUrl: String
    ) {
        val currentArticleRef = articleRef.child(articleId)
        val article = mutableMapOf<String, Any>()

        article[ARTICLE_ID] = articleId
        article[ARTICLE_TITLE] = articleTitle
        article[ARTICLE_CONTENT] = articleContent
        article[ARTICLE_IMAGE_URL] = articleImageUrl
        article[ARTICLE_CREATED_AT] = System.currentTimeMillis()
        article[WRITER_ID] = writerId
        article[WRITER_NAME] = writerName
        article[WRITER_PROFILE_IMAGE_URL] = writerProfileImageUrl

        currentArticleRef.updateChildren(article)

        hideProgress()
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1010 ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startContentProvider()
                } else {
                    Toast.makeText(this, "권한을 거부하셨습니다", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun startContentProvider() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, CONTENT_PROVIDER_REQUEST_CODE)
    }

    private fun showProgress() {

        binding.progressBar.isVisible = true
    }

    private fun hideProgress() {
        binding.progressBar.isVisible = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            CONTENT_PROVIDER_REQUEST_CODE -> {
                //data 안에 사진의 uri 가 넘어온것
                //우선 null 처리
                val uri = data?.data
                if (uri != null) {
                    binding.photoImageView.setImageURI(uri)
                    selectedUri = uri
                } else {
                    Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            LOCATION_SEARCH_REQUEST_CODE -> {
                data.let{
                    if (it != null) {
                        searchResult = it.getParcelableExtra(TMP)!!
                        Log.i("AddArticleActivity", "onActivityResult: fullAddress: ${searchResult.fullAddress}")
                        binding.locationTextView.text = searchResult.fullAddress
                    }
                    else{
                        Log.i("AddArticleActivity", "onActivityResult: 데이터를 가져오지 못함")
                        Toast.makeText(this, "위치를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    //교육용 팝업
    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 가져오기 위해 필요합니다.")
            .setPositiveButton("동의") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1010)
            }
            .create()
            .show()
    }

    companion object {
        const val LOCATION_SEARCH_REQUEST_CODE = 100
        const val CONTENT_PROVIDER_REQUEST_CODE = 200
        const val TMP = "tmp"
    }
}