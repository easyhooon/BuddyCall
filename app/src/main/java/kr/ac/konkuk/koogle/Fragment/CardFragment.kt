package kr.ac.konkuk.koogle.Fragment

import android.app.AlertDialog
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
import kr.ac.konkuk.koogle.Activity.LogInActivity
import kr.ac.konkuk.koogle.Activity.MainActivity
import kr.ac.konkuk.koogle.Adapter.CardAdapter
import kr.ac.konkuk.koogle.DBKeys
import kr.ac.konkuk.koogle.DBKeys.Companion.ARTICLE_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_ARTICLES
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_MAIN_TAGS
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_USERS
import kr.ac.konkuk.koogle.DBKeys.Companion.WRITER_ID
import kr.ac.konkuk.koogle.Model.ArticleModel
import kr.ac.konkuk.koogle.Model.BlockUserModel
import kr.ac.konkuk.koogle.Model.CardModel
import kr.ac.konkuk.koogle.R
import kr.ac.konkuk.koogle.databinding.FragmentCardBinding

class CardFragment : Fragment(R.layout.fragment_card), CardStackListener {

    var binding: FragmentCardBinding? = null

    private lateinit var cardAdapter: CardAdapter

    private val cardList = mutableListOf<CardModel>()

    private var blockList = mutableListOf<String>()

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val firebaseUser = auth.currentUser!!

    private val cardRef: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_ARTICLES)
    }

    private val currentUserRef: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_USERS).child(firebaseUser.uid)
    }

    private val currentUserBlockRef: DatabaseReference by lazy {
        currentUserRef.child(DBKeys.DB_BLOCK_USERS)
    }

    private val listener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            if (snapshot.child(WRITER_ID).value != firebaseUser.uid) {
                val cardModel = snapshot.getValue(CardModel::class.java)
                    ?: return

                if(!blockList.contains(cardModel.writerId)){
                    cardModel.tagList = snapshot.child(DB_MAIN_TAGS)
                    cardList.add(cardModel)
                }

                cardAdapter.submitList(cardList)
                cardAdapter.notifyDataSetChanged()
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

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        // Inflate the layout for this fragment
//        binding = FragmentCardBinding.inflate(layoutInflater, container, false)
//
//        initDB()
//
////        initCardStackView()
////        cardRef.addChildEventListener(listener)
//
//        return binding!!.root
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCardBinding.bind(view)

        if(auth.currentUser != null) {
            Log.i("Community fragment", "onViewCreated: ${firebaseUser.uid}")
            initDB()
//            initRecyclerView()
//            initButton()
//
//            //데이터를 가져옴
//            //addSingleValueListener -> 즉시성, 1회만 호출
//            //addChildEventListener -> 한번 등록해놓으면 계속 이벤트가 발생할때마다 등록이된다.
//            //activity 의 경우 activity 가 종료되면 이벤트가 다 날라가고 view 가 다 destroy 됨
//            //fragment 는 재사용이 되기때문에 onviewcreated 가 호출될때마다 중복으로 데이터를 가져오게됨
//            //따라서 eventlistener 를 전역으로 정의를 해놓고 viewcreated 될때마다 attach 를 하고 destroy 가 될때마다 remove 를 해주는 방식을 채택
//            articleRef.addChildEventListener(listener)
        }
        else {
            val intent = Intent(context, LogInActivity::class.java)
            activity?.startActivity(intent)
        }
    }
    private fun initDB() {
        currentUserBlockRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val blockUserModel = snapshot.getValue(BlockUserModel::class.java)
                    if (blockUserModel != null) {
                        blockList.add(blockUserModel.userId)
                    }
                }
                initCardStackView()
                cardRef.addChildEventListener(listener)

            }


            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun initCardStackView() {
        cardAdapter = CardAdapter(context)

        binding?.cardStackView?.layoutManager = CardStackLayoutManager(context, this)
        binding?.cardStackView?.adapter = cardAdapter

        manager.setStackFrom(StackFrom.Top)
        manager.setTranslationInterval(8.0f)
        manager.setSwipeThreshold(0.1f)

        cardAdapter.itemClickListener = object : CardAdapter.OnItemClickListener {
            override fun onItemChecked(
                holder: CardAdapter.ViewHolder,
                view: View,
                data: CardModel,
                position: Int
            ) {
                if (auth.currentUser != null) {
                    val intent = Intent(context, ArticleActivity::class.java)
                    intent.putExtra(ARTICLE_ID, data.articleId)

                    //fragment 에서 다른 액티비티로 데이터 전달
                    activity?.startActivity(intent)
                } else {
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
                //dialog 한번 뿌리고 진짜 삭제
                val ad = AlertDialog.Builder(context)
                ad.setMessage("해당 유저를 차단하시겠습니까? \n차단하시면 해당 유저의 글을 볼 수 없습니다.")
                ad.setPositiveButton(
                    "취소"
                ) { dialog, _ ->
                    dialog.dismiss()
                }
                ad.setNegativeButton(
                    "차단"
                ) { dialog, _ ->
                    //글과 그룹 모두 삭제
                    userBlock(data.writerId, data.writerName)
                    Toast.makeText(context, "해당 유저를 차단하였습니다", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                    dialog.dismiss()
                }
                ad.show()

            }
        }
    }

    private fun userBlock(writerId: String, writerName: String) {
        val blockId = currentUserBlockRef.push().key.toString()

        val block = mutableMapOf<String, Any>()
        block[DBKeys.USER_ID] = writerId
        block[DBKeys.USER_NAME] = writerName

        currentUserBlockRef.child(blockId).updateChildren(block)

    }


//    override fun onResume() {
//        super.onResume()
//
//        //view가 다시 보일때마다 뷰를 다시 그림
//        cardAdapter.notifyDataSetChanged()
//    }

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