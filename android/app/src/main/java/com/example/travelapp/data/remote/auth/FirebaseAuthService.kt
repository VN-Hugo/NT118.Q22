package com.example.travelapp.data.remote.auth

class FirebaseAuthService {

    private val auth = FirebaseAuth.getInstance()

    fun firebaseAuthWithGoogle(idToken: String) =
        auth.signInWithCredential(
            GoogleAuthProvider.getCredential(idToken, null)
        )
}