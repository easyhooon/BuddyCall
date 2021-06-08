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
import kr.ac.konkuk.koogle.Adapter.ArticleAdapter
import kr.ac.konkuk.koogle.DBKeys.Companion.ARTICLE_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_ARTICLES
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_USERS
import kr.ac.konkuk.koogle.Model.ArticleModel
import kr.ac.konkuk.koogle.databinding.FragmentCommunityBinding


class CommunityFragment : Fragment() {

    private var binding: FragmentCommunityBinding? = null

    private lateinit var userRef: DatabaseReference
    private lateinit var articleRef: DatabaseReference
    private lateinit var articleAdapter: ArticleAdapter

    private val articleList = mutableListOf<ArticleModel>()

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val listener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            //model 클래스 자체를 업로드하고 다운받음
            val articleModel = snapshot.getValue(ArticleModel::class.java)
            articleModel ?: return

            articleList.add(articleModel)
            articleAdapter.submitList(articleList)
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
        binding = FragmentCommunityBinding.inflate(layoutInflater, container, false)

        //초기화를 해주지 않으면 이미 값이 들어있어서 계속 해서 아이템이 추가됨

        initDB()
        initRecyclerView()
        initButton()


        //데이터를 가져옴
        //addSingleValueListener -> 즉시성, 1회만 호출
        //addChildEventListener -> 한번 등록해놓으면 계속 이벤트가 발생할때마다 등록이된다.
        //activity 의 경우 activity 가 종료되면 이벤트가 다 날라가고 view 가 다 destroy 됨
        //fragment 는 재사용이 되기때문에 onviewcreated 가 호출될때마다 중복으로 데이터를 가져오게됨
        //따라서 eventlistener 를 전역으로 정의를 해놓고 viewcreated 될때마다 attach 를 하고 destroy 가 될때마다 remove 를 해주는 방식을 채택
        articleRef.addChildEventListener(listener)

        return binding!!.root
    }

    private fun initRecyclerView() {
        articleList.clear()
        //초기화 코드
        articleAdapter = ArticleAdapter(onItemClicked = { articleModel ->
            if(auth.currentUser != null) {
                val intent = Intent(context, ArticleActivity::class.java)
                intent.putExtra(ARTICLE_ID, articleModel.articleId)

                //fragment에서 다른 액티비티로 데이터 전달
                Log.d("CommunityFragment", "articleId: ${articleModel.articleId}")
                activity?.startActivity(intent)
            }else {
                //로그인을 안한 상태
                Toast.makeText(context, "로그인 후 사용해주세요", Toast.LENGTH_LONG).show()
            }
        })


        binding!!.articleRecyclerView.layoutManager = LinearLayoutManager(context)
        binding!!.articleRecyclerView.adapter = articleAdapter
    }

    private fun initButton() {
        binding!!.btnAddArticle.setOnClickListener {

            context?.let {
                if(auth.currentUser != null){
                    startActivity(Intent(it, AddArticleActivity::class.java))
                } else {
                    Toast.makeText(context, "로그인 후 사용해주세요", Toast.LENGTH_SHORT).show()
                }

                //이것도 가능
                //startActivity(Intent(requireContext(),ArticleAddActivity::class.java))
            }
        }
    }

    private fun initDB() {
        articleRef= Firebase.database.reference.child(DB_ARTICLES)
        userRef = Firebase.database.reference.child(DB_USERS)
    }

    override fun onResume() {
        super.onResume()

        //view가 다시 보일때마다 뷰를 다시 그림
        articleAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        articleRef.removeEventListener(listener)
    }

}