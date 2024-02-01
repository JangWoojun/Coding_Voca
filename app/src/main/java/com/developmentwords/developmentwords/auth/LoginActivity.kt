package com.developmentwords.developmentwords.auth

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.developmentwords.developmentwords.R
import com.developmentwords.developmentwords.databinding.ActivityLoginBinding
import com.developmentwords.developmentwords.util.FBDB.Companion.checkGoogleLogin
import com.developmentwords.developmentwords.util.FBDB.Companion.checkTodayWord
import com.developmentwords.developmentwords.util.FBDB.Companion.downloadCourseItem
import com.developmentwords.developmentwords.util.FBDB.Companion.downloadUserInfo
import com.developmentwords.developmentwords.util.FBDB.Companion.setVersion
import com.developmentwords.developmentwords.util.ProgressUtil.createDialog
import com.developmentwords.developmentwords.util.ProgressUtil.createLoadingDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private var isPasswordHide = true
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.forgotPassword.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.password_forget_dialog)
            dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
            val windowWidth = resources.displayMetrics.widthPixels * 0.9
            dialog.window?.setLayout(windowWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)

            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

            val editText = dialog.findViewById<EditText>(R.id.email_area)
            val okButton = dialog.findViewById<Button>(R.id.ok_button)
            val cancelButton = dialog.findViewById<Button>(R.id.cancel_button)

            okButton.setOnClickListener {
                val enteredText = editText.text.toString()
                sendPasswordResetEmail(enteredText)
                dialog.dismiss()
            }

            cancelButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        binding.moveSignInText.paintFlags = binding.moveSignInText.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.moveSignInButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
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

        binding.loginButton.setOnClickListener {
            val email = binding.emailArea.text.toString()
            val password = binding.passwordArea.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일 혹은 비밀번호 영역이 비었습니다", Toast.LENGTH_SHORT).show()
            } else if (!isValidEmail(email)) {
                Toast.makeText(this, "유효한 이메일 주소가 아닙니다", Toast.LENGTH_SHORT).show()
            } else if (password.length < 6) {
                Toast.makeText(this, "비밀번호는 6자리 이상이여야 합니다", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        val loadingDialog = createLoadingDialog(this)
                        loadingDialog.show()

                        if (task.isSuccessful) {
                            downloadUserInfo(applicationContext) {
                                checkTodayWord(applicationContext) {
                                    downloadCourseItem(applicationContext) {
                                        setVersion {
                                            runOnUiThread {
                                                loadingDialog.dismiss()
                                                createDialog(this,true)
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            val exception = task.exception
                            if (exception is FirebaseAuthException) {
                                loadingDialog.dismiss()
                                Toast.makeText(this, "이메일 혹은 비밀번호가 잘못되었습니다", Toast.LENGTH_SHORT).show()
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
                loginButton.isEnabled = false
                moveSignInButton.isEnabled = false
                forgotPassword.isEnabled = false
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
                loginButton.isEnabled = true
                moveSignInButton.isEnabled = true
                forgotPassword.isEnabled = true
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
                loginButton.isEnabled = true
                moveSignInButton.isEnabled = true
                forgotPassword.isEnabled = true
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
                    loginButton.isEnabled = true
                    moveSignInButton.isEnabled = true
                    forgotPassword.isEnabled = true
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

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "비밀번호 재설정 이메일이 성공적으로 보내졌습니다", Toast.LENGTH_SHORT).show()
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthInvalidUserException) {
                        Toast.makeText(this, "이메일이 등록되지 않은 사용자입니다", Toast.LENGTH_SHORT).show()
                    } else {
                        createDialog(this,false)
                    }
                }
            }
    }
}