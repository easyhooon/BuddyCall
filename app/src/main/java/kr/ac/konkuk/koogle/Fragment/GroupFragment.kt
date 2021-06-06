package kr.ac.konkuk.koogle.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kr.ac.konkuk.koogle.Activity.ChatRoomActivity
import kr.ac.konkuk.koogle.Adapter.ChatListAdapter
import kr.ac.konkuk.koogle.Model.ChatListModel
import kr.ac.konkuk.koogle.databinding.FragmentChatListBinding


class GroupFragment : Fragment() {

    private var binding : FragmentChatListBinding? = null

    private lateinit var chatListAdapter: ChatListAdapter

    private val chatRoomList = mutableListOf<ChatListModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChatListBinding.inflate(layoutInflater, container, false)

        chatListAdapter = ChatListAdapter(onItemClicked = { chatRoom ->
            //채팅방으로 이동 하는 코드
            //this 불가능
            //context를 넣자니 nullable
            //따라서 null check걸어주고 그안에서 구현
            context?.let {
                val intent = Intent(it, ChatRoomActivity::class.java)
                intent.putExtra("chatKey", chatRoom.key)
                startActivity(intent)
            }
        })
        chatRoomList.clear()

        binding!!.chatListRecyclerView.adapter = chatListAdapter
        binding!!.chatListRecyclerView.layoutManager = LinearLayoutManager(context)

        return binding!!.root
    }

}