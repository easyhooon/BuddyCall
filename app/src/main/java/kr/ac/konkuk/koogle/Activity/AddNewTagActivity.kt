package kr.ac.konkuk.koogle.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kr.ac.konkuk.koogle.DBKeys
import kr.ac.konkuk.koogle.databinding.ActivityAddTagBinding
import kr.ac.konkuk.koogle.DBKeys.Companion

class AddNewTagActivity:AppCompatActivity() {
    lateinit var rootRef:DatabaseReference
    lateinit var tagRef:DatabaseReference
    // 처음 5명이 사용하기 전까지는 추천에 뜨지 않게 하기
    val TAG_INIT_NUM = -5

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

    fun init() {
        initDB()
        initButton()
    }
    private fun initDB(){
        rootRef = Firebase.database.reference
        tagRef = rootRef.child(DBKeys.DB_MAIN_TAGS)
    }

    private fun commitMainTag(mainTag: String): Boolean{
        // 이미 DB에 있는 태그인지 확인한다
        tagRef.child(DBKeys.TAG_NAME).equalTo(mainTag).get().addOnSuccessListener {
            // 있으면 tagNum만 늘어난다.
            Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            // 없으면 추가하고 false 반환
            val tagId = tagRef.push().key.toString()
            val newTag = mutableMapOf<String, Any>()
            Toast.makeText(this, "NO", Toast.LENGTH_SHORT).show()

            newTag[DBKeys.TAG_ID] = tagId
            newTag[DBKeys.TAG_NAME] = mainTag
            newTag[DBKeys.USED_NUM] = TAG_INIT_NUM
            tagRef.child(tagId).updateChildren(newTag)
        }
        /*
        val query = tagRef.orderByChild(DBKeys.TAG_NAME).equalTo(mainTag)
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {


            })
        }*/
        return false
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

    private fun initButton() {
        // 임시
        binding.searchEditText.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                commitMainTag(s.toString())
            }
        })

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}