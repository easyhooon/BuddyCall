package kr.ac.konkuk.koogle.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.ac.konkuk.koogle.DBKeys.Companion.ARTICLE_ID
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_ARTICLES
import kr.ac.konkuk.koogle.Model.ArticleModel
import kr.ac.konkuk.koogle.R
import kr.ac.konkuk.koogle.databinding.ActivityArticleBinding

class ArticleActivity : AppCompatActivity() {

    lateinit var currentArticleRef: DatabaseReference
    lateinit var binding: ActivityArticleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDB()
        initBackButton()
        initContactButton()
    }

    private fun initContactButton() {
        binding.contactButton.setOnClickListener {
            Toast.makeText(this, "채팅방이 생성되었습니다. 그룹을 확인해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initBackButton() {
        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun initDB() {
        val intent = intent
        val articleId = intent.getStringExtra(ARTICLE_ID).toString()
//        val articleId = intent.extras?.getString(ARTICLE_ID).toString()
        Log.d("Intent", "articleId: $articleId")
        currentArticleRef = Firebase.database.reference.child(DB_ARTICLES).child(articleId)

        //파이어베이스 데이터베이스의 정보 가져오기
        currentArticleRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val articleModel: ArticleModel? = snapshot.getValue(ArticleModel::class.java)
                    if (articleModel != null) {
                        Log.d("snapshot", "articleModel: $articleModel")
                        if (articleModel.articleImageUrl.isEmpty()) {
                            binding.photoImageView.visibility = View.GONE
                        } else {
                            Glide.with(binding.photoImageView)
                                .load(articleModel.articleImageUrl)
                                .into(binding.photoImageView)
                        }
                    }

                    if (articleModel != null) {
                        if (articleModel.writerProfileImageUrl.isEmpty()) {
                            binding.photoImageView.setImageResource(R.drawable.profile_image)
                        } else {
                            Glide.with(binding.profileImageView)
                                .load(articleModel.writerProfileImageUrl)
                                .into(binding.profileImageView)
                        }
                    }
                    if (articleModel != null) {
                        binding.titleTextView.text = articleModel.articleTitle
                        binding.contentTextView.text = articleModel.articleContent
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ArticleActivity, "데이터 로드 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }
}