package kr.ac.konkuk.koogle.Fragment

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.ac.konkuk.koogle.Activity.AddArticleActivity
import kr.ac.konkuk.koogle.Activity.ArticleActivity
import kr.ac.konkuk.koogle.Activity.LogInActivity
import kr.ac.konkuk.koogle.Adapter.CommunityAdapter
import kr.ac.konkuk.koogle.DBKeys
import kr.ac.konkuk.koogle.DBKeys.Companion.ARTICLE_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_ARTICLES
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_MAIN_TAGS
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_USERS
import kr.ac.konkuk.koogle.DBKeys.Companion.SUB_TAGS
import kr.ac.konkuk.koogle.Model.ArticleModel
import kr.ac.konkuk.koogle.Model.TagModel
import kr.ac.konkuk.koogle.R
import kr.ac.konkuk.koogle.databinding.FragmentCommunityBinding


class CommunityFragment : Fragment(R.layout.fragment_community) {

    private var binding: FragmentCommunityBinding? = null

    private lateinit var communityAdapter: CommunityAdapter

    private val articleList = mutableListOf<ArticleModel>()
    private var searchedArticleList = mutableListOf<ArticleModel>()

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val firebaseUser = auth.currentUser!!
    private val articleRef: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_ARTICLES)
    }

    private val listener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            //model 클래스 자체를 업로드하고 다운받음
            val articleModel = snapshot.getValue(ArticleModel::class.java)
            articleModel ?: return

            articleList.add(0, articleModel) //최신글이 위로 올라오도록
            communityAdapter.submitList(articleList)
            communityAdapter.notifyDataSetChanged()
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
//        binding = FragmentCommunityBinding.inflate(layoutInflater, container, false)
//
//        //초기화를 해주지 않으면 이미 값이 들어있어서 계속 해서 아이템이 추가됨
//
//        initRecyclerView()
//        initButton()
//
//        //데이터를 가져옴
//        //addSingleValueListener -> 즉시성, 1회만 호출
//        //addChildEventListener -> 한번 등록해놓으면 계속 이벤트가 발생할때마다 등록이된다.
//        //activity 의 경우 activity 가 종료되면 이벤트가 다 날라가고 view 가 다 destroy 됨
//        //fragment 는 재사용이 되기때문에 onviewcreated 가 호출될때마다 중복으로 데이터를 가져오게됨
//        //따라서 eventlistener 를 전역으로 정의를 해놓고 viewcreated 될때마다 attach 를 하고 destroy 가 될때마다 remove 를 해주는 방식을 채택
//        articleRef.addChildEventListener(listener)
//
//        return binding!!.root
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCommunityBinding.bind(view)

        //초기화를 해주지 않으면 이미 값이 들어있어서 계속 해서 아이템이 추가됨

        if(auth.currentUser != null) {
            Log.i("Community fragment", "onViewCreated: ${firebaseUser.uid}")
            initRecyclerView()
            initButton()

            //데이터를 가져옴
            //addSingleValueListener -> 즉시성, 1회만 호출
            //addChildEventListener -> 한번 등록해놓으면 계속 이벤트가 발생할때마다 등록이된다.
            //activity 의 경우 activity 가 종료되면 이벤트가 다 날라가고 view 가 다 destroy 됨
            //fragment 는 재사용이 되기때문에 onviewcreated 가 호출될때마다 중복으로 데이터를 가져오게됨
            //따라서 eventlistener 를 전역으로 정의를 해놓고 viewcreated 될때마다 attach 를 하고 destroy 가 될때마다 remove 를 해주는 방식을 채택
            articleRef.addChildEventListener(listener)
        }
        else {
            val intent = Intent(context, LogInActivity::class.java)
            activity?.startActivity(intent)
        }
    }

    private fun initRecyclerView() {
        articleList.clear()
        //초기화 코드
        communityAdapter = CommunityAdapter(onItemClicked = { articleModel ->
            if (auth.currentUser != null) {
                val intent = Intent(context, ArticleActivity::class.java)
                intent.putExtra(ARTICLE_ID, articleModel.articleId)

                //fragment에서 다른 액티비티로 데이터 전달
                Log.d("CommunityFragment", "articleId: ${articleModel.articleId}")
                activity?.startActivity(intent)
            } else {
                //로그인을 안한 상태
                Toast.makeText(context, "로그인 후 사용해주세요", Toast.LENGTH_LONG).show()
            }
        })

        binding!!.articleRecyclerView.layoutManager = LinearLayoutManager(context)
        binding!!.articleRecyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
        binding!!.articleRecyclerView.adapter = communityAdapter
    }

    private fun initButton() {
        binding!!.btnAddArticle.setOnClickListener {

            context?.let {
                if (auth.currentUser != null) {
                    startActivity(Intent(it, AddArticleActivity::class.java))
                } else {
                    Toast.makeText(context, "로그인 후 사용해주세요", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(it, LogInActivity::class.java))
                }

                //이것도 가능
                //startActivity(Intent(requireContext(),ArticleAddActivity::class.java))
            }
        }

        binding!!.searchImageView.setOnClickListener {
            val searchText = binding!!.searchEditText.text.toString()
            val articleRef = Firebase.database.reference.child(DB_ARTICLES)

            if (searchText.isEmpty()) {
                articleRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        articleList.clear()
                        for (article in snapshot.children) {
                            articleList.add(0, article.getValue(ArticleModel::class.java)!!)
                        }
                        communityAdapter.submitList(articleList)
                        communityAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
            } else {
                searchedArticleList.clear()
                articleRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (article in snapshot.children) {
                            for (tag in article.child(DB_MAIN_TAGS).children) {
                                var isContain = false
                                if (searchText in tag.key.toString()) {
                                    searchedArticleList.add(
                                        0,
                                        article.getValue(ArticleModel::class.java)!!
                                    )
                                    break
                                }
                                for (subtag in tag.child(SUB_TAGS).children) {
                                    if (searchText in subtag.key.toString()) {
                                        searchedArticleList.add(
                                            0,
                                            article.getValue(ArticleModel::class.java)!!
                                        )
                                        isContain = true
                                        break
                                    }
                                }
                                if (isContain) {
                                    break
                                }
                            }
                        }
                        communityAdapter.submitList(searchedArticleList)
                        communityAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
            }
        }
    }

    override fun onResume() {
        super.onResume()

        //view가 다시 보일때마다 뷰를 다시 그림
        communityAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        articleRef.removeEventListener(listener)
    }
}