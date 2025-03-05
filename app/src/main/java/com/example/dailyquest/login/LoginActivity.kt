package com.example.dailyquest.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dailyquest.MainActivity
import com.example.dailyquest.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        //파이어베이스 연동
        auth = FirebaseAuth.getInstance()

        //로그인 버튼 이벤트
        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)

            val id = findViewById<TextView>(R.id.editId).text.toString()
            val password = findViewById<EditText>(R.id.editPassword).text.toString().trim()

            if (id.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(id, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 로그인 성공 -> 메인 액티비티로 이동
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("USER_ID", id)
                        startActivity(intent)
                        finish() // 로그인 액티비티 종료
                    } else {
                        // 로그인 실패 시 예외 메시지 확인
                        val errorMessage = when (task.exception) {
                            is FirebaseAuthInvalidCredentialsException -> "이메일 형식이 올바르지 않습니다."
                            is FirebaseAuthInvalidUserException -> "존재하지 않는 이메일입니다."
                            is FirebaseAuthInvalidCredentialsException -> "비밀번호가 틀렸습니다."
                            else -> "로그인 실패"
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
        }

        //회원가입 버튼 이벤트
        val signupButton = findViewById<Button>(R.id.enterToSignUpPageButton)
        signupButton.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}