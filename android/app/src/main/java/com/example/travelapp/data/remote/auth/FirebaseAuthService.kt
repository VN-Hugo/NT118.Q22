package com.example.travelapp.data.remote.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
class FirebaseAuthService {

    private val auth = FirebaseAuth.getInstance()

    fun firebaseAuthWithGoogle(idToken: String) =
        auth.signInWithCredential(
            GoogleAuthProvider.getCredential(idToken, null)
        )
}