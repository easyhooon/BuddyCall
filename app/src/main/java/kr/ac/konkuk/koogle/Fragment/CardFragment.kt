package kr.ac.konkuk.koogle.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yuyakaido.android.cardstackview.*
import kr.ac.konkuk.koogle.Activity.ArticleActivity
import kr.ac.konkuk.koogle.Adapter.ArticleAdapter
import kr.ac.konkuk.koogle.Adapter.CardAdapter
import kr.ac.konkuk.koogle.DBKeys
import kr.ac.konkuk.koogle.DBKeys.Companion.ARTICLE_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_ARTICLES
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_USERS
import kr.ac.konkuk.koogle.DBKeys.Companion.WRITER_ID
import kr.ac.konkuk.koogle.Model.CardModel
import kr.ac.konkuk.koogle.databinding.FragmentCardBinding

class CardFragment : Fragment(), CardStackListener {


    var binding: FragmentCardBinding? = null

    private lateinit var userRef: DatabaseReference
    private lateinit var cardRef: DatabaseReference

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private lateinit var cardAdapter: CardAdapter

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

//        cardAdapter = CardAdapter(onItemChecked = { cardModel ->
//            if(auth.currentUser != null) {
//                val intent = Intent(context, ArticleActivity::class.java)
//                intent.putExtra(DBKeys.ARTICLE_ID, cardModel.articleId)
//
//                //fragment 에서 다른 액티비티로 데이터 전달
//                activity?.startActivity(intent)
//            }else {
//                //로그인을 안한 상태
//                Toast.makeText(context, "로그인 후 사용해주세요", Toast.LENGTH_LONG).show()
//            }
//        })

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
//        binding?.cancelImageView?.setOnClickListener {
//
//        }
//
//        binding?.checkImageView?.setOnClickListener {
//            val setting = SwipeAnimationSetting.Builder()
//                .setDirection(Direction.Right)
//                .setDuration(Duration.Normal.duration)
//                .setInterpolator(AccelerateInterpolator())
//                .build()
//            manager.setSwipeAnimationSetting(setting)
//            binding?.cardStackView?.swipe()
//        }
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
        cardAdapter = CardAdapter()

        binding?.cardStackView?.layoutManager = CardStackLayoutManager(context, this)
        binding?.cardStackView?.adapter = cardAdapter

        manager.setStackFrom(StackFrom.Top)
        manager.setTranslationInterval(8.0f)
        manager.setSwipeThreshold(0.1f)

        cardAdapter.itemClickListener = object: CardAdapter.OnItemClickListener{
            override fun onItemChecked(
                holder: CardAdapter.ViewHolder,
                view: View,
                data: CardModel,
                position: Int
            ) {
                if(auth.currentUser != null) {
                    val intent = Intent(context, ArticleActivity::class.java)
                    intent.putExtra(ARTICLE_ID, data.articleId)

                    //fragment 에서 다른 액티비티로 데이터 전달
                    activity?.startActivity(intent)
                }else {
                    //로그인을 안한 상태
                    Toast.makeText(context, "로그인 후 사용해주세요", Toast.LENGTH_LONG).show()
                }
            }

            override fun onItemCanceled(
                holder: CardAdapter.ViewHolder,
                view: View,
                data: CardModel,
                position: Int
            ) {

            }

        }
    }

    override fun onCardSwiped(direction: Direction?) {}
    override fun onCardDragging(direction: Direction?, ratio: Float) {}
    override fun onCardRewound() {}
    override fun onCardCanceled() {}
    override fun onCardAppeared(view: View?, position: Int) {}
    override fun onCardDisappeared(view: View?, position: Int) {}
}