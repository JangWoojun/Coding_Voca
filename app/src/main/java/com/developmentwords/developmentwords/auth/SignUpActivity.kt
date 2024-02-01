package com.developmentwords.developmentwords.auth

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.developmentwords.developmentwords.R
import com.developmentwords.developmentwords.databinding.ActivitySignUpBinding
import com.developmentwords.developmentwords.util.FBDB.Companion.checkGoogleLogin
import com.developmentwords.developmentwords.util.FBDB.Companion.downloadCourseItem
import com.developmentwords.developmentwords.util.FBDB.Companion.newUser
import com.developmentwords.developmentwords.util.FBDB.Companion.setVersion
import com.developmentwords.developmentwords.util.ProgressUtil.createDialog
import com.developmentwords.developmentwords.util.ProgressUtil.createLoadingDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private var isPasswordHide = true
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.moveLoginText.paintFlags = binding.moveLoginText.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.moveLoginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.passwordShowHideButton.bringToFront()
        binding.passwordShowHideButton.setOnClickListener {
            isPasswordHide = if (isPasswordHide) {
                binding.passwordShowHideButtonImage.setImageResource(R.drawable.show_icon)
                binding.passwordArea.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                false
            } else {
                binding.passwordShowHideButtonImage.setImageResource(R.drawable.hide_icon)
                binding.passwordArea.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                true
            }
            binding.passwordArea.setSelection(binding.passwordArea.length())
        }

        binding.signUpButton.setOnClickListener {

            val email = binding.emailArea.text.toString()
            val password = binding.passwordArea.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일 혹은 비밀번호 영역이 비었습니다", Toast.LENGTH_SHORT).show()
            } else if (!isValidEmail(email)) {
                Toast.makeText(this, "유효한 이메일 주소가 아닙니다", Toast.LENGTH_SHORT).show()
            } else if (password.length < 6) {
                Toast.makeText(this, "비밀번호는 6자리 이상이여야 합니다", Toast.LENGTH_SHORT).show()
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        val loadingDialog = createLoadingDialog(this)
                        loadingDialog.show()
                        if (task.isSuccessful) {
                            newUser(applicationContext) {
                                downloadCourseItem(applicationContext) {
                                    setVersion {
                                        runOnUiThread {
                                            loadingDialog.dismiss()
                                            createDialog(this,true)
                                        }
                                    }
                                }
                            }
                        } else {
                            val exception = task.exception
                            if (exception is FirebaseAuthUserCollisionException) {
                                loadingDialog.dismiss()
                                Toast.makeText(this, "이미 존재하는 이메일입니다", Toast.LENGTH_SHORT).show()
                            } else {
                                loadingDialog.dismiss()
                                createDialog(this,false)
                            }

                        }
                    }
            }

        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()

        binding.googleLoginButton.setOnClickListener {
            signInGoogle()
            binding.apply {
                googleLoginButton.isEnabled = false
                signUpButton.isEnabled = false
                moveLoginButton.isEnabled = false
            }
        }

        binding.appleLoginButton.setOnClickListener { // 애플 계발자 계정 등록 이후 구현
            Toast.makeText(this, "10월 이후 구현 예정입니다", Toast.LENGTH_SHORT).show()
        }

    }
    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        } else {
            binding.apply {
                googleLoginButton.isEnabled = true
                signUpButton.isEnabled = true
                moveLoginButton.isEnabled = true
            }
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            binding.apply {
                googleLoginButton.isEnabled = true
                signUpButton.isEnabled = true
                moveLoginButton.isEnabled = true
            }
            createDialog(this,false)
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            val loadingDialog = createLoadingDialog(this)
            loadingDialog.show()
            if (it.isSuccessful) {
                checkGoogleLogin(applicationContext) {
                    downloadCourseItem(applicationContext) {
                        setVersion {
                            runOnUiThread {
                                loadingDialog.dismiss()
                                createDialog(this,true)
                            }
                        }
                    }
                }
            } else {
                binding.apply {
                    googleLoginButton.isEnabled = true
                    signUpButton.isEnabled = true
                    moveLoginButton.isEnabled = true
                }
                loadingDialog.dismiss()
                createDialog(this,false)
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }
}