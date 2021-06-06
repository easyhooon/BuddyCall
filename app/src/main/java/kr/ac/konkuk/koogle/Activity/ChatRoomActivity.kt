package kr.ac.konkuk.koogle.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.ac.konkuk.koogle.Adapter.ChatAdapter
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_CHATS
import kr.ac.konkuk.koogle.Model.ChatModel
import kr.ac.konkuk.koogle.databinding.ActivityChatRoomBinding

class ChatRoomActivity : AppCompatActivity() {
    //todo 아직 미구현

    lateinit var binding: ActivityChatRoomBinding

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val chatList = mutableListOf<ChatModel>()
    private val adapter = ChatAdapter()
    private var chatRef: DatabaseReference?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val chatKey = intent.getLongExtra("chatKey", -1)

        //채팅방 KEY를 하나 받아옴
        chatRef = Firebase.database.reference.child(DB_CHATS).child("$chatKey")

        chatRef?.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatItem = snapshot.getValue(ChatModel::class.java)
                chatItem ?: return

                chatList.add(chatItem)
                //갱신
                adapter.submitList(chatList)
                //key가 없어 DiffUtil 이 제대로 동작안할 수도 있기 때문에
                //Todo key를 만들어줘야겠네
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        binding.chatRecyclerView.adapter = adapter
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.sendButton.setOnClickListener {
            val chatItem = ChatModel(
                senderId = auth.currentUser!!.uid,
                message = binding.messageEditText.text.toString()
            )
            chatRef?.push()?.setValue(chatItem)
        }
    }
}