package kr.ac.konkuk.koogle.Activity

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import kotlinx.android.synthetic.main.activity_add_article.*
import kr.ac.konkuk.koogle.Adapter.AddArticleImageAdapter
import kr.ac.konkuk.koogle.DBKeys.Companion.ADMIN_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.ADMIN_NAME
import kr.ac.konkuk.koogle.DBKeys.Companion.ADMIN_PROFILE_IMAGE_URL
import kr.ac.konkuk.koogle.DBKeys.Companion.ARTICLE_CONTENT
import kr.ac.konkuk.koogle.DBKeys.Companion.ARTICLE_CREATED_AT
import kr.ac.konkuk.koogle.DBKeys.Companion.ARTICLE_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.ARTICLE_IMAGE_PATH
import kr.ac.konkuk.koogle.DBKeys.Companion.ARTICLE_IMAGE_URL
import kr.ac.konkuk.koogle.DBKeys.Companion.ARTICLE_TITLE
import kr.ac.konkuk.koogle.DBKeys.Companion.CURRENT_NUMBER
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_ARTICLES
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_GROUPS
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_USERS
import kr.ac.konkuk.koogle.DBKeys.Companion.DESIRED_LOCATION
import kr.ac.konkuk.koogle.DBKeys.Companion.GROUP_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.GROUP_LAST_CHAT
import kr.ac.konkuk.koogle.DBKeys.Companion.GROUP_LAST_CHAT_CREATED_AT
import kr.ac.konkuk.koogle.DBKeys.Companion.RECRUITMENT_NUMBER
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

    private lateinit var selectedUriList: ArrayList<Uri>
    private lateinit var downloadedUrlList: ArrayList<String>

    private lateinit var imageAdapter: AddArticleImageAdapter

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val firebaseUser = auth.currentUser!!

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
    private val userGroupRef: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_USERS).child(firebaseUser.uid).child(DB_GROUPS).child(articleId)
    }
    private val currentGroupRef: DatabaseReference by lazy {
        groupRef.child(articleId)
    }

    private lateinit var writerName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDB()
        initButton()
        initImageRecyclerView()
    }

    private fun initDB() {
        val currentUserRef = userRef.child(auth.currentUser?.uid.toString())
        currentUserRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val userModel: UserModel? = snapshot.getValue(UserModel::class.java)
                if (userModel != null) {
                    Log.d("onDataChange", "userName: ${userModel.userName}")
                    writerName = userModel.userName
                    writerProfileImageUrl = userModel.userProfileImageUrl
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("onCancelled: ", "데이터로드 실패")
            }

        })
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
            val recruitmentNumberText = binding.recruitmentNumberEditText.text.toString()
            val recruitmentNumber = recruitmentNumberText.toInt()

            if (articleTitle.isEmpty()) {
                Toast.makeText(this, "글의 제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (articleContent.isEmpty()) {
                Toast.makeText(this, "글의 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (recruitmentNumberText.isEmpty()) {
                Toast.makeText(this, "모집인원을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showProgress()

            //중간에 이미지가 있으면 업로드 과정을 추가
            selectedUriList = arrayListOf()
            selectedUriList = imageAdapter.getUriList()
            downloadedUrlList = arrayListOf<String>()

            if (selectedUriList.isNotEmpty()) {
                for ((i, uri) in selectedUriList.withIndex()) {
                    uploadPhoto(uri, i,
                        //내부 비동기
                        successHandler = { uploadedUri ->
                            updateImage(uploadedUri)
                        },
                        errorHandler = {
                            Toast.makeText(this, "사진 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
            uploadArticle(
                writerId,
                writerName,
                writerProfileImageUrl,
                articleId,
                articleTitle,
                articleContent,
                recruitmentNumber,
                downloadedUrlList
            )
            createChatRoom(
                writerId,
                writerName,
                writerProfileImageUrl,
                articleTitle,
                recruitmentNumber
            )

            addUserGroup(articleId, articleTitle)
        }

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.locationAddButton.setOnClickListener {
            val intent = Intent(this, LocationSearchActivity::class.java)
            startActivityForResult(intent, LOCATION_SEARCH_REQUEST_CODE)
        }
    }

    private fun addUserGroup(articleId: String, articleTitle: String) {
        val userGroup = mutableMapOf<String, Any>()
        userGroup[GROUP_ID] = articleId
        userGroup[ARTICLE_TITLE] = articleTitle
        userGroupRef.updateChildren(userGroup)
    }

    private fun createChatRoom(
        adminId: String,
        adminName: String,
        adminProfileImageUrl: String,
        articleTitle: String,
        recruitmentNumber: Int)
    {
        val group = mutableMapOf<String, Any>()

        group[GROUP_ID] = articleId
        group[ARTICLE_TITLE] = articleTitle
        group[ADMIN_ID] = adminId
        group[ADMIN_NAME] = adminName
        group[ADMIN_PROFILE_IMAGE_URL] = adminProfileImageUrl
        group[RECRUITMENT_NUMBER] = recruitmentNumber
        group[CURRENT_NUMBER] = 1
        group[GROUP_LAST_CHAT] = ""
        group[GROUP_LAST_CHAT_CREATED_AT] = 0

//        currentGroupRef.setValue(group)
        currentGroupRef.updateChildren(group)

        //채팅방 생성 후에 방장(admin)이 채팅방에 참여자로 등록

        //채팅방은 유저 ref와 메세지 ref로 구성
        val currentGroupUserRef = currentGroupRef.child(DB_USERS).child(adminId)
        val user = mutableMapOf<String, Any>()
        user[USER_ID] = adminId
        user[USER_NAME] = adminName
        user[USER_PROFILE_IMAGE_URL] = adminProfileImageUrl

//        currentGroupUserRef.setValue(user)
        currentGroupUserRef.updateChildren(user)

        hideProgress()
        finish()

    }

    private fun uploadPhoto(uri: Uri, num: Int, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
        //파일명이 중복이 되지 않도록
        //이렇게 두면 이거 삭제는 어떻게 하남
        val fileName = "${articleId}_$num.jpg"
        storage.reference.child(ARTICLE_IMAGE_PATH).child(fileName)
            .putFile(uri)
            //성공했는지 여부를 체크
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    storage.reference.child(ARTICLE_IMAGE_PATH).child(fileName).downloadUrl
                        .addOnSuccessListener { uploadedUri ->
                            successHandler(uploadedUri.toString())
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
        recruitmentNumber: Int,
        uriList: ArrayList<String>
    ) {
        val currentArticleRef = articleRef.child(articleId)
        val article = mutableMapOf<String, Any>()

        article[ARTICLE_ID] = articleId
        article[ARTICLE_TITLE] = articleTitle
        article[ARTICLE_CONTENT] = articleContent
        article[ARTICLE_IMAGE_URL] = uriList
        article[ARTICLE_CREATED_AT] = System.currentTimeMillis()
        article[WRITER_ID] = writerId
        article[WRITER_NAME] = writerName
        article[WRITER_PROFILE_IMAGE_URL] = writerProfileImageUrl
        article[RECRUITMENT_NUMBER] = recruitmentNumber
        article[CURRENT_NUMBER] = 1
        article[DESIRED_LOCATION] = searchResult

//        currentArticleRef.updateChildren(article)
        currentArticleRef.setValue(article)

        hideProgress()
        finish()
    }

    private fun updateImage(uri: String) {
        val imageRef = articleRef.child(articleId).child(ARTICLE_IMAGE_URL)
        downloadedUrlList.add(uri)
        imageRef.setValue(downloadedUrlList)
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
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, ""), CONTENT_PROVIDER_REQUEST_CODE)
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
                if (data?.clipData != null) {
                    initImageRecyclerView()

                    val clipData = data.clipData!!

                    when (clipData.itemCount) {
                        1 -> {
                            imageAdapter.addItem(data.clipData!!.getItemAt(0).uri)
                        }
                        in 2..9 -> {
                            for (i in 0 until clipData.itemCount) {
                                imageAdapter.addItem(clipData.getItemAt(i).uri)
                            }
                        }
                        else -> {
                            Toast.makeText(this, "사진은 최대 10개 선택 가능합니다.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    if (binding.imageRecyclerView.visibility == View.GONE)
                        binding.imageRecyclerView.visibility = View.VISIBLE
                }
                else {
                    Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            LOCATION_SEARCH_REQUEST_CODE -> {
                if(resultCode == RESULT_OK){
                    searchResult = data?.getParcelableExtra(SEARCH_RESULT_FINAL)!!
                    binding.locationTextView.text = searchResult.fullAddress
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

    private fun initImageRecyclerView() {
        //val oldUriList = imageAdapter.getUriList()
        binding.imageRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imageAdapter = AddArticleImageAdapter()
        imageAdapter.itemClickListener = object : AddArticleImageAdapter.OnItemClickListener {
            override fun OnItemClick(
                holder: AddArticleImageAdapter.ViewHolder
            ) {
                imageAdapter.removeItem(holder.adapterPosition)
                if (imageAdapter.getUriList().isEmpty())
                    binding.imageRecyclerView.visibility = View.GONE
            }
        }
        //for (uri in oldUriList)
            //imageAdapter.addItem(uri)
        binding.imageRecyclerView.adapter = imageAdapter

        val simpleCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT,
            ItemTouchHelper.UP or ItemTouchHelper.DOWN) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                imageAdapter.moveItem(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        }

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.imageRecyclerView)
    }

    companion object {
        const val LOCATION_SEARCH_REQUEST_CODE = 100
        const val CONTENT_PROVIDER_REQUEST_CODE = 200
        const val SEARCH_RESULT_FINAL = "SEARCH_RESULT_FINAL"
    }
}