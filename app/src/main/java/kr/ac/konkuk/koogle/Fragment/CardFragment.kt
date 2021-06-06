package kr.ac.konkuk.koogle.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.StackFrom
import kr.ac.konkuk.koogle.Adapter.CardAdapter
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_ARTICLES
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_USERS
import kr.ac.konkuk.koogle.DBKeys.Companion.WRITER_ID
import kr.ac.konkuk.koogle.Model.CardModel
import kr.ac.konkuk.koogle.databinding.FragmentCardBinding

//todo 아직 작동 불가능, 데이터가 안불러와짐

class CardFragment : Fragment(), CardStackListener {


    var binding: FragmentCardBinding? = null

    private lateinit var userRef: DatabaseReference
    private lateinit var cardRef: DatabaseReference

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val cardAdapter = CardAdapter()

    private val cardList = mutableListOf<CardModel>()
    private val manager by lazy {
        CardStackLayoutManager(context, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCardBinding.inflate(layoutInflater, container, false)

        initCardStackView()
        initDB()

        return binding!!.root
    }

    private fun initDB() {

        userRef = Firebase.database.reference.child(DB_USERS)

        auth.uid?.let { userRef.child(it) }
            ?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // 유저정보를 갱신
                    getUnSelectedArticles()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        initCardStackView()
        initButton()

    }

    private fun initButton() {
        binding?.cancelImageView?.setOnClickListener {

        }

        binding?.checkImageView?.setOnClickListener {
            //todo 이거 누르면 그룹으로 가는지, 글 내용으로 가는지
        }
    }

    private fun getUnSelectedArticles() {
        //전체 유저에 대한 table 의 change 를 다 관찰
        cardRef = Firebase.database.reference.child(DB_ARTICLES)
        cardRef.addChildEventListener(object: ChildEventListener {
            //ArticleRef 안에서 발생하는 모든 변경사항들이 전부 이벤트로 떨어짐
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.child(WRITER_ID).value != auth.currentUser?.uid)
                {
                    val cardModel = snapshot.getValue(CardModel::class.java)
                    if (cardModel != null) {
                        Log.d("onChildAdded", "cardModel: $cardModel")
                        cardList.add(cardModel)
                    }
                    cardAdapter.submitList(cardList)
//                    cardAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}

        })

    }

    private fun initCardStackView() {
        binding?.cardStackView?.layoutManager = CardStackLayoutManager(context, this)
        binding?.cardStackView?.adapter = cardAdapter

        manager.setStackFrom(StackFrom.Top)
        manager.setTranslationInterval(8.0f)
        manager.setSwipeThreshold(0.1f)
    }

    override fun onCardSwiped(direction: Direction?) {}
    override fun onCardDragging(direction: Direction?, ratio: Float) {}
    override fun onCardRewound() {}
    override fun onCardCanceled() {}
    override fun onCardAppeared(view: View?, position: Int) {}
    override fun onCardDisappeared(view: View?, position: Int) {}
}