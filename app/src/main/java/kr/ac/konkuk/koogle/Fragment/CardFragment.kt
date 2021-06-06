package kr.ac.konkuk.koogle.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import kr.ac.konkuk.koogle.Adapter.CardAdapter
import kr.ac.konkuk.koogle.DBKeys
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_USERS
import kr.ac.konkuk.koogle.DBKeys.Companion.DIS_LIKE
import kr.ac.konkuk.koogle.DBKeys.Companion.LIKE
import kr.ac.konkuk.koogle.DBKeys.Companion.LIKED_BY
import kr.ac.konkuk.koogle.DBKeys.Companion.USER_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.USER_NAME
import kr.ac.konkuk.koogle.Model.CardModel
import kr.ac.konkuk.koogle.databinding.FragmentCardBinding

//todo 아직 작동 불가능

class CardFragment : Fragment(), CardStackListener {


    var binding: FragmentCardBinding? = null

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val adapter = CardAdapter()
    private lateinit var userRef: DatabaseReference

    private val cardItems = mutableListOf<CardModel>()
    private val manager by lazy {
        CardStackLayoutManager(context, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCardBinding.inflate(layoutInflater, container, false)

        initDB()
        initCardStackView()

        return binding!!.root
    }

    private fun initDB() {
        //currentUserDB에서 값을 가져오는 방법 -> 리스너를 통해
        //single value에 대한 event만 받아올 것이기 때문에
        //onCreate에서 한번만 불러옴
        userRef = Firebase.database.reference.child(DB_USERS)

        auth.currentUser?.let { userRef.child(it.uid) }
            ?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // 유저정보를 갱신
                    //todo 글 정보를 갱신
//                    getUnSelectedUsers()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

//    private fun getUnSelectedUsers() {
//        //전체 유저에 대한 table의 change를 다 관찰
//        userRef.addChildEventListener(object: ChildEventListener {
//            //userDB안에서 발생하는 모든 변경사항들이 전부 이벤트로 떨어짐
//            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                if (snapshot.child(USER_ID).value != getCurrentUserID()
//                    && snapshot.child(LIKED_BY).child(LIKE).hasChild(getCurrentUserID()).not()
//                    && snapshot.child(LIKED_BY).child(DIS_LIKE).hasChild(getCurrentUserID()).not())
//                {
//                    //지금 현재 보고있는 유저의 id가 나와 id가 같지 않고, 상대방의 like와 dislike에 둘다 내가 없을때 => 이 유저는 내가 한번도 선택한 적이 없는 User이다
//                    val userId = snapshot.child(USER_ID).value.toString()
//                    var name = "undecided"
//                    if (snapshot.child(USER_NAME).value != null) {
//                        name = snapshot.child(USER_NAME).value.toString()
//                    }
//
////                    cardItems.add(CardModel(userId, name))
//                    adapter.submitList(cardItems)
//                    adapter.notifyDataSetChanged()
//                }
//            }
//
//            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                //이름이 바뀌거나 유저가 다른 유저를 like했을때 갱신
//                //변경된 유저를 cardItems에서 찾음
//                cardItems.find{it.userId == snapshot.key}?.let{
////                    it.name = snapshot.child("name").value.toString()
//                }
//                adapter.submitList(cardItems)
//                adapter.notifyDataSetChanged()
//            }
//
//            override fun onChildRemoved(snapshot: DataSnapshot) {}
//            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
//            override fun onCancelled(error: DatabaseError) {}
//
//        })
//    }

    private fun initCardStackView() {
        binding?.cardStackView?.layoutManager = CardStackLayoutManager(context, this)
        binding?.cardStackView?.adapter = adapter
    }

    override fun onCardSwiped(direction: Direction?) {

    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {}

    override fun onCardRewound() {}

    override fun onCardCanceled() {}

    override fun onCardAppeared(view: View?, position: Int) {}

    override fun onCardDisappeared(view: View?, position: Int) {}
}