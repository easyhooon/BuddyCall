package kr.ac.konkuk.koogle.Activity

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import kr.ac.konkuk.koogle.DBKeys.Companion.DB_USERS
import kr.ac.konkuk.koogle.Model.UserModel
import kr.ac.konkuk.koogle.R
import kr.ac.konkuk.koogle.databinding.ActivityAccountInfoBinding

class AccountInfoActivity : AppCompatActivity() {

    lateinit var binding: ActivityAccountInfoBinding

    //파이어베이스 인증 객체 초기화
    //java에서 Firebase.getInstance()와 같이 Firebase Auth를 initialize 해주는 코드
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val userRef: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_USERS).child(firebaseUser.uid)
    }

    //DB 객체 초기화
    private val firebaseUser = auth.currentUser!!
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (auth.currentUser != null) {
            //초기화
            initButton()
            initUserInfo()
        }
        else{
            //로그인 되지 않음
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun initUserInfo() {
        //입력 로그인용 유저의 데이터를 불러오기 위한 uid
       firebaseUser.uid


//        파이어베이스 데이터베이스의 정보 가져오기

            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userModel: UserModel? = snapshot.getValue(UserModel::class.java)

                        if (userModel != null) {
                            if (userModel.userProfileImageUrl.isEmpty()) {
                                binding.ivProfileImage.setImageResource(R.drawable.profile_image)
                            } else {
                                Glide.with(binding.ivProfileImage)
                                    .load(userModel.userProfileImageUrl)
                                    .into(binding.ivProfileImage)
                            }
                        }
                        if (userModel != null) {
                            binding.tvNickname.text = userModel.userName
                        }
                        if (userModel != null) {
                            binding.tvAccount.text = userModel.userEmail
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun initButton() {

        //로그아웃 버튼을 누르면 로그아웃이 되고 LogInActivity 로 돌아감
        binding.btnLogout.setOnClickListener { //파이어베이스에 연동된 계정 로그아웃 처리
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@AccountInfoActivity, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnDeleteAccount.setOnClickListener {
            val ad = AlertDialog.Builder(this@AccountInfoActivity)
            ad.setMessage("정말 회원 탈퇴를 하시겠습니까?")
            ad.setPositiveButton(
                "취소"
            ) { dialog, _ ->
                Toast.makeText(this@AccountInfoActivity, "회원 탈퇴가 취소되었습니다", Toast.LENGTH_SHORT)
                    .show()
                dialog.dismiss()
            }
            ad.setNegativeButton(
                "탈퇴"
            ) { dialog, _ ->
                deleteAccount()
                Toast.makeText(this@AccountInfoActivity, "회원 탈퇴가 완료되었습니다", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@AccountInfoActivity, LogInActivity::class.java)
                startActivity(intent)
                finish()
                dialog.dismiss()
            }
            ad.show()
        }

        binding.backButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    //todo 수정 필요
    //User 탈퇴하면 그 유저가 쓴거 다 날려야되네?
    private fun deleteAccount() {
        val deleteRef: StorageReference = storageRef.child("profile images/${firebaseUser.uid}.jpg")
        Log.d(ContentValues.TAG, "onDataChange: desertRef: $deleteRef")
        deleteRef.delete().addOnSuccessListener {
            Toast.makeText(
                this@AccountInfoActivity,
                "계정을 삭제하였습니다.",
                Toast.LENGTH_SHORT
            ).show()
        }.addOnFailureListener {
            Toast.makeText(this, "계정을 삭제하는데 실패하였습니디", Toast.LENGTH_SHORT).show();
        }
        firebaseUser.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Firebase.database.reference.child(DB_USERS).child(firebaseUser.uid).setValue(null)
            } else {
                val message = task.exception.toString()
                Toast.makeText(this@AccountInfoActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}