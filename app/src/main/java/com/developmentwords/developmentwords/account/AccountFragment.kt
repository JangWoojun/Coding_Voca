package com.developmentwords.developmentwords.account

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.developmentwords.developmentwords.R
import com.developmentwords.developmentwords.databinding.FragmentAccountBinding
import com.developmentwords.developmentwords.util.FBAuth
import com.developmentwords.developmentwords.util.AppDatabase
import com.developmentwords.developmentwords.util.FBDB.Companion.deleteData
import com.developmentwords.developmentwords.util.FBDB.Companion.uploadUserInfo
import com.developmentwords.developmentwords.util.ProgressUtil.createDialog
import com.developmentwords.developmentwords.util.ProgressUtil.createLoadingDialog
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var storage: FirebaseStorage
    private var editNameChk = true
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storage = Firebase.storage

        binding.apply {
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(requireContext())
                val user = db?.userDao()!!.getUser()
                val imageUri = user.userImage

                withContext(Dispatchers.Main) {
                    nameArea.text = Editable.Factory.getInstance().newEditable(user.userName)
                    if (imageUri == "null") {
                        Glide.with(requireContext())
                            .load(R.drawable.default_avatar)
                            .apply(RequestOptions.formatOf(DecodeFormat.PREFER_ARGB_8888))
                            .into(binding.profile)
                    } else {
                        Glide.with(requireContext())
                            .load(imageUri)
                            .apply(RequestOptions.circleCropTransform())
                            .apply(RequestOptions.formatOf(DecodeFormat.PREFER_ARGB_8888))
                            .into(binding.profile)
                    }
                }
            }
            inquiryButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_SENDTO)
                    .apply {
                        type = "text/plain"
                        data = Uri.parse("mailto:")
                        val context = """
                        < 유저정보 >
                        OS 버전: ${Build.VERSION.SDK_INT}
                        기종: ${Build.BRAND} ${Build.MODEL}
                        고유 ID: ${FBAuth.getUid()}
                        
                        /**
                        앱에 대한 건의 사항이나 문의 사항을 작성해주세요 
                        버그를 제보할 때 버그 화면을 함께 첨부해주시면 
                        개발자가 버그를 고치는데 큰 도움이 됩니다
    
                        코딩 보카를 이용해주셔서 감사합니다!
                        **/
                        """.trimIndent()
                        putExtra(Intent.EXTRA_EMAIL, arrayOf("woojun0107@naver.com"))
                        putExtra(Intent.EXTRA_SUBJECT, "코딩보카 문의")
                        putExtra(Intent.EXTRA_TEXT, context)
                    }
                startActivity(Intent.createChooser(intent, "메일 전송하기"))
            }

            logoutButton.setOnClickListener {
                deleteData(this@AccountFragment, false)
            }

            withdrawalButton.setOnClickListener {
                deleteData(this@AccountFragment, true)
            }

            privacyPolicyButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sch10719.neocities.org/codingvocaprivacypolicy"))
                startActivity(intent)
            }

            editButton.setOnClickListener {
                if (editNameChk) {
                    nameArea.requestFocus()
                    nameArea.setSelection(nameArea.text.length)
                    editNameChk = false
                    nameArea.isEnabled = true
                    editButtonImage.setImageResource(R.drawable.check)
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        val db = AppDatabase.getDatabase(requireContext())
                        val user = db?.userDao()!!.getUser()
                        user.userName = nameArea.text.toString()
                        db.userDao().updateUser(user)
                        editNameChk = true
                        withContext(Dispatchers.Main) {
                            editButtonImage.setImageResource(R.drawable.edit)
                            nameArea.isEnabled = false
                        }
                        uploadUserInfo(requireContext())
                    }
                }
            }

            profileLayout.setOnClickListener {
                openGallery()
            }

        }
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri, onSuccess: (String) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("images/${FBAuth.getUid()}.jpg")

        val uploadTask = imageRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                onSuccess(downloadUri.toString())
            } else {
                createDialog(requireContext(), false)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val dialog = createLoadingDialog(requireContext())
        dialog.show()

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data

            if (imageUri != null) {
                uploadImageToFirebaseStorage(imageUri) { imageUrl ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val db = AppDatabase.getDatabase(requireContext())
                        val user = db?.userDao()!!.getUser()

                        user.userImage = imageUrl

                        db.userDao().updateUser(user)

                        withContext(Dispatchers.Main) {
                            dialog.dismiss()
                            if (imageUrl == "null") {
                                Glide.with(requireContext())
                                    .load(R.drawable.default_avatar)
                                    .apply(RequestOptions.formatOf(DecodeFormat.PREFER_ARGB_8888))
                                    .into(binding.profile)
                            } else {
                                Glide.with(requireContext())
                                    .load(imageUrl)
                                    .apply(RequestOptions.circleCropTransform())
                                    .apply(RequestOptions.formatOf(DecodeFormat.PREFER_ARGB_8888))
                                    .into(binding.profile)
                            }
                        }
                        uploadUserInfo(requireContext())
                    }
                }
            } else {
                dialog.dismiss()
            }
        } else {
            dialog.dismiss()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}