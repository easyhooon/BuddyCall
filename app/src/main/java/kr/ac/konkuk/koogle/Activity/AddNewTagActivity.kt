package kr.ac.konkuk.koogle.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
    // 처음 5명이 사용하기 전까지는 추천에 뜨지 않게 하기
    val TAG_INIT_NUM = 5

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
            tagRef.orderByChild(DBKeys.USED).addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    setDataRecyclerView(snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            return
        }
        // 리스트 만들기
        val list: MutableList<TagModel> = ArrayList<TagModel>()
        for (s in snapshot!!.children) {
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

/*
    private fun commitMainTag(mainTag: String): Boolean{
        tagRef.orderByChild(DBKeys.TAG_ID)
            .equalTo(mainTag).addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.childrenCount>0){
                        // 있으면 tagNum만 늘어난다.
                        tagRef.child(mainTag).child(DBKeys.USED_NUM).setValue(
                            snapshot.child(mainTag).child(DBKeys.USED_NUM).value.toString().toInt() + 1
                        )

                    }else{
                        // 없으면 추가하고 false 반환
                        val newTag = mutableMapOf<String, Any>()

                        newTag[DBKeys.TAG_ID] = mainTag
                        newTag[DBKeys.USED_NUM] = TAG_INIT_NUM
                        tagRef.child(mainTag).updateChildren(newTag)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        return false
    }
*/
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
        // 임시
        /*
        binding.addNewTagBtn.setOnClickListener {
            val text = binding.searchEditText.text.toString()
            if(text.length<2){
                Toast.makeText(this, "태그를 2자 이상 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            commitMainTag(binding.searchEditText.text.toString())
            binding.searchEditText.text.clear()
        }
        */
        binding.apply {
            backButton.setOnClickListener {
                val intent = Intent(this@AddNewTagActivity, MainActivity::class.java)
                startActivity(intent)
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
}