package kr.ac.konkuk.koogle.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.ac.konkuk.koogle.Activity.AddArticleActivity
import kr.ac.konkuk.koogle.Activity.ArticleActivity
import kr.ac.konkuk.koogle.Activity.ChatRoomActivity
import kr.ac.konkuk.koogle.Adapter.ArticleAdapter
import kr.ac.konkuk.koogle.Adapter.GroupAdapter
import kr.ac.konkuk.koogle.DBKeys
import kr.ac.konkuk.koogle.DBKeys.Companion.GROUP_ID
import kr.ac.konkuk.koogle.Model.ArticleModel
import kr.ac.konkuk.koogle.Model.GroupModel
import kr.ac.konkuk.koogle.databinding.FragmentGroupBinding

//todo 아이템을 길게 눌르면 옆에 체크 할 수 있는 박스가 나와서 나가기가 가능하게?

class GroupFragment : Fragment() {

    private var binding : FragmentGroupBinding? = null

    private lateinit var groupRef:DatabaseReference
    private lateinit var userRef:DatabaseReference
    private lateinit var groupAdapter: GroupAdapter

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val groupList = mutableListOf<GroupModel>()

    private val listener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            //model 클래스 자체를 업로드하고 다운받음
            val groupModel = snapshot.getValue(GroupModel::class.java)
            groupModel ?: return

            groupList.add(groupModel)
            groupAdapter.submitList(groupList)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onChildRemoved(snapshot: DataSnapshot) {}
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) {}

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentGroupBinding.inflate(layoutInflater, container, false)

        groupAdapter = GroupAdapter(onItemClicked = { groupModel ->
            //채팅방으로 이동 하는 코드
            //this 불가능
            //context 를 넣자니 nullable
            //따라서 null check 걸어주고 그안에서 구현
            context?.let {
                val intent = Intent(it, ChatRoomActivity::class.java)
                intent.putExtra(GROUP_ID, groupModel.groupId)
                activity?.startActivity(intent)
            }
        })
        groupList.clear()

        groupRef= Firebase.database.reference.child(DBKeys.DB_GROUPS)
        userRef = Firebase.database.reference.child(DBKeys.DB_USERS)
        //초기화 코드
        groupAdapter = GroupAdapter(onItemClicked = { groupModel ->
            if(auth.currentUser != null) {
                val intent = Intent(context, ChatRoomActivity::class.java)
                intent.putExtra(GROUP_ID, groupModel.groupId)

                activity?.startActivity(intent)
            }else {
                //로그인을 안한 상태
                Toast.makeText(context, "로그인 후 사용해주세요", Toast.LENGTH_LONG).show()
            }
        })

        binding!!.groupRecyclerView.layoutManager = LinearLayoutManager(context)
        binding!!.groupRecyclerView.adapter = groupAdapter


        //데이터를 가져옴
        //addSingleValueListener -> 즉시성, 1회만 호출
        //addChildEventListener -> 한번 등록해놓으면 계속 이벤트가 발생할때마다 등록이된다.
        //activity 의 경우 activity 가 종료되면 이벤트가 다 날라가고 view 가 다 destroy 됨
        //fragment 는 재사용이 되기때문에 onviewcreated 가 호출될때마다 중복으로 데이터를 가져오게됨
        //따라서 eventlistener 를 전역으로 정의를 해놓고 viewcreated 될때마다 attach 를 하고 destroy 가 될때마다 remove 를 해주는 방식을 채택
        groupRef.addChildEventListener(listener)

        return binding!!.root
    }

}