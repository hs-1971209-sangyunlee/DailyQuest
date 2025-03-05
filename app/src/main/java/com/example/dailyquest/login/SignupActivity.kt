package com.example.dailyquest.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dailyquest.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        auth = FirebaseAuth.getInstance()

        //뒤로가기 버튼 이벤트
        val backButton = findViewById<Button>(R.id.backToLoginPageButton)
        backButton.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        //회원가입 버튼 이벤트
        val signupButton = findViewById<Button>(R.id.signupButton)
        signupButton.setOnClickListener{
            val email = findViewById<EditText>(R.id.signupId).text.toString().trim()
            val password = findViewById<EditText>(R.id.signupPassword).text.toString().trim()
            val passwordCheck = findViewById<EditText>(R.id.signupPasswordChecking).text.toString().trim()
            // 이메일 & 비밀번호 입력 여부 확인
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if(!password.equals(passwordCheck)){
                Toast.makeText(this, "비밀번호를 정확히 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()  // 회원가입 성공 후 종료
                    } else {
                        // 회원가입 실패 시 예외 메시지 확인
                        val errorMessage = when (task.exception) {
                            is FirebaseAuthUserCollisionException -> "이미 등록된 이메일입니다."
                            is FirebaseAuthWeakPasswordException -> "비밀번호는 최소 6자 이상이어야 합니다."
                            is FirebaseAuthInvalidCredentialsException -> "이메일 형식이 올바르지 않습니다."
                            else -> "회원가입 실패"
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}