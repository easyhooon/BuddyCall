package kr.ac.konkuk.koogle.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kr.ac.konkuk.koogle.Adapter.TagAdapter
import kr.ac.konkuk.koogle.DBKeys
import kr.ac.konkuk.koogle.Model.TagModel

/*
    2021-06-19 주예진 작성
    ProfileActivity, EditProfileActivity 공통된 점 묶어서 만듬
 */
abstract class ProfileCommonActivity: AppCompatActivity(){
    private val maxShowTag = 20 // 최대 허용 태그 개수
    //파이어베이스 인증 객체 초기화
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    //DB 객체 초기화
    open val firebaseUser = auth.currentUser!!
    open val storage = FirebaseStorage.getInstance()
    lateinit var userTagRef: DatabaseReference
    lateinit var imageUri: Uri
    lateinit var fileRef: StorageReference

    // 태그 리사이클러뷰 관련
    lateinit var tagAdapter: TagAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(auth.currentUser == null){
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }
        initRecyclerView()
    }

    // 리사이클러뷰 초기화
    protected fun initRecyclerView(){
        // DB 에서 유저 태그 데이터 받아옴
        val tagData: ArrayList<TagModel> = arrayListOf()
        userTagRef = Firebase.database.reference
            .child(DBKeys.DB_USER_TAG).child(firebaseUser.uid)
        userTagRef.orderByChild(DBKeys.TAG_INDEX)
            .limitToFirst(maxShowTag).addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(s in snapshot.children){
                        val newSubTag = arrayListOf<String>()
                        for(st in s.child(DBKeys.SUB_TAGS).children){
                            newSubTag.add(st.key.toString())
                        }
                        tagData.add(
                            TagModel(
                                s.key.toString(), newSubTag,
                                s.child(DBKeys.TAG_VALUE).value.toString().toInt(),
                                s.child(DBKeys.TAG_TYPE).value.toString().toInt()
                            ))
                    }
                    // 로딩 작업이 끝난 이후 RecyclerView 를 초기화하는 순서를 맞추기 위해 이곳에 넣음
                    initTagRecyclerView(tagData)
                }
                override fun onCancelled(error: DatabaseError) {
                    return
                }
            })
    }

    // DB 에서 불러온 내용을 바탕으로 리사이클러뷰 설정 (initRecyclerView 에서 호출됨)
    abstract fun initTagRecyclerView(data: ArrayList<TagModel>)
}