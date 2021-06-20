package kr.ac.konkuk.koogle.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.ac.konkuk.koogle.Adapter.AddTagAdapter
import kr.ac.konkuk.koogle.DBKeys
import kr.ac.konkuk.koogle.Model.TagModel
import kr.ac.konkuk.koogle.Model.TagType
import kr.ac.konkuk.koogle.R
import kr.ac.konkuk.koogle.databinding.ActivityAddTagBinding
import java.util.*
import kotlin.collections.HashMap


/*
    2021-06-17 주예진 작성
    새로운 tag 를 추가하는 액티비티
 */
class AddNewTagActivity:AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: AddTagAdapter
    lateinit var rootRef:DatabaseReference
    lateinit var tagRef:DatabaseReference

    //Firebase Auth를 initialize 해주는 코드
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    lateinit var binding: ActivityAddTagBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTagBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        initDB()
        initView()
        initRecyclerView()
    }
    private fun initDB(){
        rootRef = Firebase.database.reference
        tagRef = rootRef.child(DBKeys.DB_MAIN_TAGS)
    }

    // 쿼리 결과를 리사이클러뷰로 전달
    // null 을 전달함으로써 모든 내용을 표시하는 기본 쿼리를 이용한다.
    private fun setDataRecyclerView(snapshot: DataSnapshot?){
        if(snapshot==null){
            // DB 에서 한 번만 Tag table 을 받아와서 Adapter 에 전달
            tagRef.orderByChild(DBKeys.USED).addListenerForSingleValueEvent(
                object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                    setDataRecyclerView(snapshot)
                }
                override fun onCancelled(error: DatabaseError) {}
            })
            return
        }
        // 리스트 만들기
        val list: MutableList<TagModel> = ArrayList<TagModel>()
        for (s in snapshot.children) {
            // 만약 얼마 쓰이지 않은(=Used 가 음수가 아닌 경우) 태그라면 아예 없는 취급한다.
                if(s.child(DBKeys.USED).value.toString().toInt()>=0) continue
            // 태그로 더 많이 쓰였으면 태그형으로 보여주고, 수치로 더 많이 쓰였으면 수치로 보여준다.
            val usedTags = s.child(DBKeys.USED_TAGS).value.toString().toInt()
            val usedValue = s.child(DBKeys.USED_VALUE).value.toString().toInt()
            val tagType: Int = if(usedTags >= usedValue)TagType.TAG else TagType.VALUE
            // Tag 의 key 값으로 name 이 들어감
            val tagModel = TagModel(s.key!!, tagType)
            list.add(tagModel)
        }
        adapter = AddTagAdapter(this, list)
        recyclerView.adapter = adapter
    }

    private fun initRecyclerView(){
        val context = this
        recyclerView = binding.addTagRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this,
        LinearLayoutManager.VERTICAL, false)
        setDataRecyclerView(null)
    }

    private fun commitSubTag(mainTag: String): Boolean{
        // 이미 DB에 있는 태그인지 확인한다

        // 있으면 사용 회수 늘리고  true 반환

        // 없으면 추가하고 true 반환
        return false
    }

    private fun commitTag(mainTag: String, subTag:String){
        // tagId
        // val tagRef = tagRef.child()
        val tag = mutableMapOf<String, Any>()
    }

    private fun initView() {
        binding.apply {
            // 새 태그 버튼: 검색 창에 있는 태그가 DB에 있으면 무시, 없으면 목록에 추가한다
            // sub Tag 는 DB 에는 아직 추가하지 않음. 유저가 최종적으로 프로필에 적용했을 때 추가한다
            addNewTagBtn.setOnClickListener {
                // 입력 창에 아무 것도 없으면 무시
                if(searchEditText.length() == 0){
                    setDataRecyclerView(null)
                    return@setOnClickListener
                }
                val text = searchEditText.text.toString()
                tagRef.orderByKey().equalTo(text).addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // DB 에 같은 값이 있음: 무시
                        if(snapshot.childrenCount>0){
                            Toast.makeText(this@AddNewTagActivity,
                                R.string.msg_exist_tag, Toast.LENGTH_SHORT).show()
                            return
                        }else{
                            // 없으면 목록에 추가한다
                            val newTag = mutableMapOf<String, Any>()
                            newTag[DBKeys.USED] = TAG_INIT_NUM
                            newTag[DBKeys.USED_TAGS] = 0
                            newTag[DBKeys.USED_VALUE] = 0
                            tagRef.child(text).updateChildren(newTag)
                        }
                        setDataRecyclerView(snapshot)
                        // 검색창 비우지 않기: 추가된 것 바로 보여주도록!
                        // searchEditText.text.clear()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }
            backButton.setOnClickListener {
                val intent = Intent(this@AddNewTagActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            cancelBtn.setOnClickListener {
                finish()
            }
            commitBtn.setOnClickListener {
                val intentR = Intent()
                val selectedTags: HashMap<String, TagModel> = adapter.selectedList
                if (selectedTags.isNotEmpty()){
                    intentR.putExtra("selectedTags", selectedTags) //사용자에게 입력받은값 넣기
                    setResult(RESULT_OK, intentR) //결과를 저장
                }

                finish() //액티비티 종료
            }
            searchEditText.setOnFocusChangeListener { v, hasFocus ->
                if(hasFocus)
                    searchImageView.visibility = View.GONE
                else
                    searchImageView.visibility = View.VISIBLE
            }
            // 입력에 따라 실시간으로 태그들이 검색됨
            searchEditText.addTextChangedListener(object :TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if(searchEditText.length() == 0){
                        setDataRecyclerView(null)
                        return
                    }
                    // 쿼리를 리사이클러뷰로 전달
                    // DB 에서 한 번만 Tag table 을 받아와서 Adapter 에 전달
                    tagRef.orderByKey().startAt(searchEditText.text.toString()).addListenerForSingleValueEvent(object:ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            setDataRecyclerView(snapshot)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                }

            })
        }
    }
    companion object{
        // 처음 5명이 사용하기 전까지는 추천에 뜨지 않게 하기
        const val TAG_INIT_NUM = 5
    }
}