package kr.ac.konkuk.koogle.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yuyakaido.android.cardstackview.*
import kr.ac.konkuk.koogle.Activity.ArticleActivity
import kr.ac.konkuk.koogle.Adapter.CardAdapter
import kr.ac.konkuk.koogle.DBKeys.Companion.ARTICLE_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_ARTICLES
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_USERS
import kr.ac.konkuk.koogle.DBKeys.Companion.WRITER_ID
import kr.ac.konkuk.koogle.Model.ArticleModel
import kr.ac.konkuk.koogle.Model.CardModel
import kr.ac.konkuk.koogle.databinding.FragmentCardBinding

class CardFragment : Fragment(), CardStackListener {

    var binding: FragmentCardBinding? = null

    private lateinit var cardAdapter: CardAdapter

    private val cardList = mutableListOf<CardModel>()

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val firebaseUser = auth.currentUser!!

    private val cardRef: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_ARTICLES)
    }

    private val listener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            if (snapshot.child(WRITER_ID).value != firebaseUser.uid)
            {
                val cardModel = snapshot.getValue(CardModel::class.java)
                if (cardModel != null) {
                    cardList.add(cardModel)
                }
                cardAdapter.submitList(cardList)
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onChildRemoved(snapshot: DataSnapshot) {}
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) {}
    }

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

        cardRef.addChildEventListener(listener)

        return binding!!.root
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
                //todo 구현
            }
        }
    }

    override fun onResume() {
        super.onResume()

        //view가 다시 보일때마다 뷰를 다시 그림
        cardAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        cardRef.removeEventListener(listener)
    }

    override fun onCardSwiped(direction: Direction?) {}
    override fun onCardDragging(direction: Direction?, ratio: Float) {}
    override fun onCardRewound() {}
    override fun onCardCanceled() {}
    override fun onCardAppeared(view: View?, position: Int) {}
    override fun onCardDisappeared(view: View?, position: Int) {}
}