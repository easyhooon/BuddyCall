package kr.ac.konkuk.koogle.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.ac.konkuk.koogle.Adapter.ChatAdapter
import kr.ac.konkuk.koogle.Adapter.ChatAdapter.Companion.LEFT_CHAT
import kr.ac.konkuk.koogle.Adapter.ChatAdapter.Companion.RIGHT_CHAT
import kr.ac.konkuk.koogle.DBKeys.Companion.CHAT_CONTENT
import kr.ac.konkuk.koogle.DBKeys.Companion.CHAT_CREATED_AT
import kr.ac.konkuk.koogle.DBKeys.Companion.CHAT_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_GROUPS
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_MESSAGES
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_USERS
import kr.ac.konkuk.koogle.DBKeys.Companion.GROUP_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.WRITER_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.WRITER_NAME
import kr.ac.konkuk.koogle.DBKeys.Companion.WRITER_PROFILE_IMAGE_URL
import kr.ac.konkuk.koogle.Model.ChatModel
import kr.ac.konkuk.koogle.Model.UserModel
import kr.ac.konkuk.koogle.databinding.ActivityChatRoomBinding

class ChatRoomActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatRoomBinding

    private lateinit var chatRef:DatabaseReference

    private lateinit var writerName:String

    private lateinit var writerId:String

    private lateinit var writerProfileImageUrl:String

    private lateinit var chatId:String

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val FirebaseUser = auth.currentUser!!

    private val chatList = mutableListOf<ChatModel>()

    private val chatAdapter = ChatAdapter(this, chatList)

    private val listener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            //model 클래스 자체를 업로드하고 다운받음
            val chatModel = snapshot.getValue(ChatModel::class.java)
            chatModel ?: return

            if (chatModel.writerId == FirebaseUser.uid){
                chatModel.viewType = RIGHT_CHAT
            }
            else
                chatModel.viewType = LEFT_CHAT

            chatList.add(chatModel)
            chatAdapter.notifyDataSetChanged()
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
        initButton()

        chatRef.addChildEventListener(listener)
    }

    private fun initButton() {
        binding.sendButton.setOnClickListener {
            writerId = auth.currentUser?.uid.toString()
            val content = binding.messageEditText.text.toString()

            showProgress()

            sendChat(writerId, writerName, writerProfileImageUrl, content)

            binding.messageEditText.text = null
        }
    }

    private fun sendChat(writerId: String, writerName: String, writerProfileImageUrl: String, content: String) {
        chatId = chatRef.push().key.toString()
        val currentChatRef = chatRef.child(chatId)
        val message = mutableMapOf<String, Any>()

        message[CHAT_ID] = chatId
        message[WRITER_ID] = writerId
        message[WRITER_NAME] = writerName
        message[WRITER_PROFILE_IMAGE_URL] = writerProfileImageUrl
        message[CHAT_CONTENT] = content
        message[CHAT_CREATED_AT] = System.currentTimeMillis()

        currentChatRef.updateChildren(message)

        hideProgress()
    }

    private fun initDB() {
        val intent = intent
        val groupId = intent.getStringExtra(GROUP_ID).toString()

        chatRef = Firebase.database.reference.child(DB_GROUPS).child(groupId).child(DB_MESSAGES)

        binding.chatRecyclerView.adapter = chatAdapter
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)

        val currentUserRef = Firebase.database.reference.child(DB_USERS).child(auth.currentUser?.uid.toString())
        currentUserRef.addListenerForSingleValueEvent(object: ValueEventListener {
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

    private fun showProgress() {
        binding.progressBar.isVisible = true
    }

    private fun hideProgress() {
        binding.progressBar.isVisible = false
    }
}