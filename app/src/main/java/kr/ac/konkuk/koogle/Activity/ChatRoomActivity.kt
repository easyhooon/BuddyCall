package kr.ac.konkuk.koogle.Activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat_room.*
import kr.ac.konkuk.koogle.Adapter.ChatAdapter
import kr.ac.konkuk.koogle.DBKeys.Companion.ADMIN_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.CHAT_CONTENT
import kr.ac.konkuk.koogle.DBKeys.Companion.CHAT_CREATED_AT
import kr.ac.konkuk.koogle.DBKeys.Companion.CHAT_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.CURRENT_NUMBER
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_ARTICLES
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_GROUPS
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_MESSAGES
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_USERS
import kr.ac.konkuk.koogle.DBKeys.Companion.GROUP_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.GROUP_LAST_CHAT
import kr.ac.konkuk.koogle.DBKeys.Companion.GROUP_LAST_CHAT_CREATED_AT
import kr.ac.konkuk.koogle.DBKeys.Companion.LEFT_CHAT
import kr.ac.konkuk.koogle.DBKeys.Companion.RIGHT_CHAT
import kr.ac.konkuk.koogle.DBKeys.Companion.WRITER_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.WRITER_NAME
import kr.ac.konkuk.koogle.DBKeys.Companion.WRITER_PROFILE_IMAGE_URL
import kr.ac.konkuk.koogle.Model.ChatModel
import kr.ac.konkuk.koogle.Model.GroupModel
import kr.ac.konkuk.koogle.Model.UserModel
import kr.ac.konkuk.koogle.R
import kr.ac.konkuk.koogle.databinding.ActivityChatRoomBinding

class ChatRoomActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatRoomBinding

    private var userIdList: MutableList<String> = mutableListOf()

    private lateinit var writerName: String

    private lateinit var writerId: String

    private lateinit var writerProfileImageUrl: String

    private lateinit var chatId: String

    private lateinit var groupId: String

    private lateinit var currentNumber: String

    private lateinit var adminId: String

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val firebaseUser = auth.currentUser!!

    private val userRef: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_USERS)
    }

    private val currentArticleRef: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_ARTICLES).child(groupId)
    }

    private val currentGroupRef: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_GROUPS).child(groupId)
    }

    private val currentUserRef: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_USERS).child(firebaseUser.uid)
    }

    //전체 그룹 맴버
    private val currentGroupUsersRef: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_GROUPS).child(groupId).child(DB_USERS)
    }

    //현재 로그인한 그룹맴버
    private val currentGroupUserRef: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_GROUPS).child(groupId).child(DB_USERS)
            .child(firebaseUser.uid)
    }

    private val chatRef: DatabaseReference by lazy {
        currentGroupRef.child(DB_MESSAGES)
    }

    private val currentChatRef: DatabaseReference by lazy {
        chatRef.child(chatId)
    }

    private val currentUserGroupRef: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_USERS).child(firebaseUser.uid).child(DB_GROUPS)
    }

    private val chatList = mutableListOf<ChatModel>()

    private val chatAdapter = ChatAdapter(this, chatList)

    private val listener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            //model 클래스 자체를 업로드하고 다운받음
            val chatModel = snapshot.getValue(ChatModel::class.java)
            chatModel ?: return

            if (chatModel.writerId == firebaseUser.uid) {
                chatModel.viewType = RIGHT_CHAT
            } else
                chatModel.viewType = LEFT_CHAT

            chatList.add(chatModel)
            chatAdapter.notifyDataSetChanged()
            binding.chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1);
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onChildRemoved(snapshot: DataSnapshot) {}
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) {}

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDB()
        initViews()
        initButton()

        chatRef.addChildEventListener(listener)
    }

    private fun initViews() {
        binding.chatRecyclerView.adapter = chatAdapter
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)

        setSupportActionBar(binding.articleToolbar)
        val actionBar = supportActionBar!!
        actionBar.apply {
            setDisplayShowCustomEnabled(true)
            setDisplayShowTitleEnabled(false) //기본 제목을 없애줌
            setDisplayHomeAsUpEnabled(true) // 자동으로 뒤로가기 버튼 만들어줌
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(::writerId.isInitialized){
            if(firebaseUser.uid == writerId){
                val menuInflater = menuInflater
                menuInflater.inflate(R.menu.chat_admin_option_menu, menu)
            }
            else{
                val menuInflater = menuInflater
                menuInflater.inflate(R.menu.chat_option_menu, menu)
            }
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.exitChatRoom -> {
                if (firebaseUser.uid == adminId) {
                    //방장인 경우
                    //글, 그룹 삭제하는 걸로
                    //dialog 한번 뿌리고 진짜 삭제
                    val ad = AlertDialog.Builder(this@ChatRoomActivity)
                    ad.setMessage("정말 글과 그룹을 삭제하시겠습니까?")
                    ad.setPositiveButton(
                        "아니오"
                    ) { dialog, _ ->
                        dialog.dismiss()
                    }
                    ad.setNegativeButton(
                        "네"
                    ) { dialog, _ ->
                        //글과 그룹 모두 삭제
                        deleteArticle()
                        val intent = Intent(this@ChatRoomActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        dialog.dismiss()
                    }
                    ad.show()
                } else {
                    //방장이 아닌 경우
                    //dialog 한번 뿌리고 확인 누르면 진짜 나가기
                    val ad = AlertDialog.Builder(this@ChatRoomActivity)
                    ad.setMessage("정말 그룹을 나가시겠습니까?")
                    ad.setPositiveButton(
                        "아니오"
                    ) { dialog, _ ->
                        dialog.dismiss()
                    }
                    ad.setNegativeButton(
                        "네"
                    ) { dialog, _ ->
                        exitChatRoom()
                        val intent = Intent(this@ChatRoomActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        dialog.dismiss()
                    }
                    ad.show()
                }

            }

            R.id.adminEvaluate -> {
                val intent = Intent(this@ChatRoomActivity, MainActivity::class.java)
                intent.putExtra(ADMIN_ID, adminId)
                startActivity(intent)
                finish()
            }
            else -> {
                //뒤로가기
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun exitChatRoom() {
        //currentNumber 갱신
        currentGroupUserRef.setValue(null)
        val group = mutableMapOf<String, Any>()
        group[CURRENT_NUMBER] = currentNumber.toInt() - 1
        currentGroupRef.updateChildren(group)

        val article = mutableMapOf<String, Any>()
        article[CURRENT_NUMBER] = currentNumber.toInt() - 1
        currentArticleRef.updateChildren(article)

        //유저의 그룹목록에서 현재 그룹을 삭제
        currentUserGroupRef.setValue(null)
    }

    private fun deleteArticle() {
        //모든 유저의 group참여 목록에서 이 그룹을 없애야되는데?
        //해당 그룹에 참여했던 유저 목록을 list로 가져와서 걔네로 반복문 돌리면 될듯

        //이거 먼저 동기적으로 수행하고
        for (userId in userIdList) {
            userRef.child(userId).child(DB_GROUPS).child(groupId).setValue(null)
        }

        currentArticleRef.setValue(null)
        currentGroupRef.setValue(null)
    }

    private fun initButton() {
        binding.messageEditText.addTextChangedListener {
            if (it.toString() == "") {
                binding.sendButton.isEnabled = false
            } else {
                binding.sendButton.isEnabled = true
            }
        }

        binding.sendButton.setOnClickListener {
            writerId = auth.currentUser?.uid.toString()
            val content = binding.messageEditText.text.toString()

            showProgress()

            sendChat(writerId, writerName, writerProfileImageUrl, content)

            binding.messageEditText.text = null
        }
    }

    private fun sendChat(
        writerId: String,
        writerName: String,
        writerProfileImageUrl: String,
        content: String
    ) {
        chatId = chatRef.push().key.toString()
        val message = mutableMapOf<String, Any>()

        message[CHAT_ID] = chatId
        message[WRITER_ID] = writerId
        message[WRITER_NAME] = writerName
        message[WRITER_PROFILE_IMAGE_URL] = writerProfileImageUrl
        message[CHAT_CONTENT] = content
        message[CHAT_CREATED_AT] = System.currentTimeMillis()

        currentChatRef.updateChildren(message)

        val group = mutableMapOf<String, Any>()
        group[GROUP_LAST_CHAT] = content
        group[GROUP_LAST_CHAT_CREATED_AT] = System.currentTimeMillis()

        currentGroupRef.updateChildren(group)

        hideProgress()
    }

    private fun initDB() {
        val intent = intent
        groupId = intent.getStringExtra(GROUP_ID).toString()
        currentGroupRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val groupModel: GroupModel? = snapshot.getValue(GroupModel::class.java)
                if (groupModel != null) {
                    adminId = groupModel.adminId
                    currentNumber = groupModel.currentNumber.toString()
                    binding.chatTitleTextView.text = groupModel.articleTitle
                    binding.currentNumberTextView.text = currentNumber
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("onCancelled: ", "데이터로드 실패")
            }

        })

        currentUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
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

        //그룹에 속한 유저 id 목록을 가져옴
        currentGroupUsersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val userModel = snapshot.getValue(UserModel::class.java)
                    if (userModel != null) {
                        userIdList.add(userModel.userId)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("onCancelled: ", "데이터로드 실패")
            }

        })
    }

    private fun showProgress() {
        binding.progressBar.isVisible = true
    }

    private fun hideProgress() {
        binding.progressBar.isVisible = false
    }
}