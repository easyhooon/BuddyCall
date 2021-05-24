package kr.ac.konkuk.koogle

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.ac.konkuk.koogle.DBKeys.Companion.USERS
import kr.ac.konkuk.koogle.DBKeys.Companion.USER_ID
import kr.ac.konkuk.koogle.databinding.ActivityLogInBinding

class LogInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    //페이스북 로그인버튼을 눌르면 페이스북 앱이 열림, 그리고 로그인 완료가 되면 다시 액티비티로 넘어옴(Activity Callback) -> onActivityResult가 열림
    // 페이스북에 갔다가 onActivityResult에서 가져온 값을 다시 페이스북 sdk에 전달을 해 줌으로써 실제로 로그인이 되었는지를 판단함
    private lateinit var callbackManager: CallbackManager

    lateinit var binding: ActivityLogInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        //초기화
        callbackManager = CallbackManager.Factory.create()

        initLoginButton()
        initSignUpButton()
        initFacebookLoginButton()
    }

    private fun initSignUpButton() {
        binding.signUpButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun initLoginButton() {
        binding.loginButton.setOnClickListener {
            Log.d("log", "initLoginButton: 눌렀음")
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if(email.isEmpty()){
                Toast.makeText(this, ENTER_EMAIL, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(password.isEmpty()){
                Toast.makeText(this, ENTER_PASSWORD, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        //로그인 액티비티의 기능 수행 완료
                        handleSuccessLogin()
                    } else {
                        Toast.makeText(this, LOGIN_FAIL, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun initFacebookLoginButton() {
        //유저에게 가져올 정보 -> 이메일과 프로필
        binding.facebookLoginButton.setPermissions(EMAIL, PROFILE)
        binding.facebookLoginButton.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onCancel() {

                }
                override fun onSuccess(result: LoginResult) {
                    // 로그인이 성공적 -> 로그인 엑세스 토큰을 가져온 다음에 파이어베이스에 넘김
                    // 정상적으로 로그인 되었을 때는 result로 null이 내려오지 않을 것이기 때문에 result에 ?을 제거
                    val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener(this@LogInActivity) { task ->
                            if (task.isSuccessful) {
                                handleSuccessLogin()
                            } else {
                                Toast.makeText(this@LogInActivity, FACEBOOK_LOGIN_FAIL, Toast.LENGTH_SHORT).show()
                            }
                        }
                }
                override fun onError(error: FacebookException?) {
                    //콜백함수안에 있기 때문에 @LogInActivity에서 this를 가져왔다고 명시를 해줘야함
                    Toast.makeText(this@LogInActivity, FACEBOOK_LOGIN_FAIL, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun handleSuccessLogin() {
        if(auth.currentUser == null) {
            startActivity(Intent(this, LogInActivity::class.java))
            return
        } else {
            //currentUser는 nullable이기 때문에 위에 예외처리하였음
            val userId = auth.currentUser?.uid.orEmpty()
            val userName = auth.currentUser?.displayName.orEmpty()
            //reference가 최상위-> child child로 경로 지정
            //경로가 존재하지 않으면 생성, 있으면 그 경로를 가져옴
            val currentUserDB = Firebase.database.reference.child(USERS).child(userId)
            val user = mutableMapOf<String, Any>()
            user[USER_ID] = userId
            user[USER_NAME] = userName
            currentUserDB.updateChildren(user)

            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        const val EMAIL = "email"
        const val PROFILE = "public_profile"
        const val USER_NAME = "user_name"
        const val LOGIN_FAIL = "로그인에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요."
        const val FACEBOOK_LOGIN_FAIL = "페이스북 로그인이 실패했습니다"
        const val ENTER_EMAIL = "이메일을 입력해주세요"
        const val ENTER_PASSWORD = "비밀번호를 입력해주세요"
    }
}