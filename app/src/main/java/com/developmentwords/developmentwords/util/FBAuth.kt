package com.developmentwords.developmentwords.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FBAuth {
    companion object {
        private lateinit var auth: FirebaseAuth

        fun getUid(): String {
            auth = Firebase.auth

            return auth.currentUser?.uid.toString()
        }

        fun getUser(): FirebaseUser {
            auth = Firebase.auth

            return auth.currentUser!!
        }

        fun logOut() {
            auth = Firebase.auth

            auth.signOut()
        }

    }
}