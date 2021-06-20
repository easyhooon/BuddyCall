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
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.coroutines.*
import kr.ac.konkuk.koogle.Activity.ChatRoomActivity
import kr.ac.konkuk.koogle.Adapter.GroupAdapter
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_GROUPS
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_USERS
import kr.ac.konkuk.koogle.DBKeys.Companion.GROUP_ID
import kr.ac.konkuk.koogle.Model.GroupModel
import kr.ac.konkuk.koogle.Model.UserModel
import kr.ac.konkuk.koogle.R
import kr.ac.konkuk.koogle.databinding.FragmentGroupBinding

class GroupFragment : Fragment(R.layout.fragment_group) {

    private var binding: FragmentGroupBinding? = null

    val scope = CoroutineScope(Dispatchers.Main)

    private lateinit var groupAdapter: GroupAdapter

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val firebaseUser = auth.currentUser!!

    private val groupRef: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_GROUPS)
    }

    private val currentUserGroupRef: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_USERS).child(firebaseUser.uid).child(DB_GROUPS)
    }

    private val userGroupList = mutableListOf<String>()
    private val groupList = mutableListOf<GroupModel>()

    private val listener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            //model 클래스 자체를 업로드하고 다운받음
            val groupModel = snapshot.getValue(GroupModel::class.java)
            groupModel ?: return
            //로그인 한 유저가 속해있는 그룹만 보여줌
            //그러기 위해선 로그인 한 유저가 어떤 그룹에 속해있는지를 알아야함
            //전체 그룹에 속해있는 모든 유저를 담아와서...
            //DB구조를 바꿀 필요가
            //유저내부에 현재 속해있는 그룹의 아이디를 저장해서 거기에 포함되있는 그룹만 가져오도록

            //유저가 속한 그룹 리스트에 포함되는 그룹만 추가
            Log.i("GroupFragment", "onChildAdded Before : ${groupModel.groupId}")

            for (groupId in userGroupList) {
                Log.i("GroupFragment", "userGroupList :  $groupId")
            }
            for (groupId in userGroupList) {
                if (groupId == groupModel.groupId) {
                    Log.i("GroupFragment", "유저가 속한 그룹의 아이디 :  ${groupModel.groupId}")
                    groupList.add(0, groupModel) //최신 그룹이 위로 올라오도록
                }
            }
//            if (userGroupList.contains(groupModel.groupId)){
//                Log.i("GroupFragment", "onChildAdded: ${groupModel.groupId}")
//                groupList.add(groupModel)
//            }
            //왜 되는거지.. 아무튼 해결
            groupAdapter.submitList(groupList)
            groupAdapter.notifyDataSetChanged()
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onChildRemoved(snapshot: DataSnapshot) {}
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) {}
    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        // Inflate the layout for this fragment
//        binding = FragmentGroupBinding.inflate(layoutInflater, container, false)
//
//        groupAdapter = GroupAdapter(onItemClicked = { groupModel ->
//            //채팅방으로 이동 하는 코드
//            //this 불가능
//            //context 를 넣자니 nullable
//            //따라서 null check 걸어주고 그안에서 구현
//            context?.let {
//                val intent = Intent(it, ChatRoomActivity::class.java)
//                intent.putExtra(GROUP_ID, groupModel.groupId)
//                activity?.startActivity(intent)
//            }
//        })
//
//        initDB()
//        initRecyclerView()
//
//        //데이터를 가져옴
//        //addSingleValueListener -> 즉시성, 1회만 호출
//        //addChildEventListener -> 한번 등록해놓으면 계속 이벤트가 발생할때마다 등록이된다.
//        //activity 의 경우 activity 가 종료되면 이벤트가 다 날라가고 view 가 다 destroy 됨
//        //fragment 는 재사용이 되기때문에 onviewcreated 가 호출될때마다 중복으로 데이터를 가져오게됨
//        //따라서 eventlistener 를 전역으로 정의를 해놓고 viewcreated 될때마다 attach 를 하고 destroy 가 될때마다 remove 를 해주는 방식을 채택
//        groupRef.addChildEventListener(listener)
//
//        return binding!!.root
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inflate the layout for this fragment
        binding = FragmentGroupBinding.bind(view)

        initDB()
//        initRecyclerView()

        //데이터를 가져옴
        //addSingleValueListener -> 즉시성, 1회만 호출
        //addChildEventListener -> 한번 등록해놓으면 계속 이벤트가 발생할때마다 등록이된다.
        //activity 의 경우 activity 가 종료되면 이벤트가 다 날라가고 view 가 다 destroy 됨
        //fragment 는 재사용이 되기때문에 onviewcreated 가 호출될때마다 중복으로 데이터를 가져오게됨
        //따라서 eventlistener 를 전역으로 정의를 해놓고 viewcreated 될때마다 attach 를 하고 destroy 가 될때마다 remove 를 해주는 방식을 채택
//        groupRef.addChildEventListener(listener)
    }

    private fun initDB() {
        currentUserGroupRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val groupModel = snapshot.getValue(GroupModel::class.java)
                    Log.i("GroupFragment", "groupModel: $groupModel")
                    if (groupModel != null) {
                        userGroupList.add(groupModel.groupId)
                    }
                }
                //동기적 실행을 위해 위치 옮김
                initRecyclerView()

//                scope.launch {
//                    delay(1000)
//                    groupRef.addChildEventListener(listener)
//                }
                groupRef.addChildEventListener(listener)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun initRecyclerView() {
        groupList.clear()

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

        binding!!.groupRecyclerView.layoutManager = LinearLayoutManager(context)
        binding!!.groupRecyclerView.adapter = groupAdapter
    }

//    override fun onResume() {
//        super.onResume()
//
//        //view가 다시 보일때마다 뷰를 다시 그림
//        groupAdapter.notifyDataSetChanged()
//    }

    override fun onDestroyView() {
        super.onDestroyView()

        groupRef.removeEventListener(listener)
    }
}