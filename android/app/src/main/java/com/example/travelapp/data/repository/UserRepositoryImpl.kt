package com.example.travelapp.data.repository

import android.net.Uri
import com.example.travelapp.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) : UserRepository {

    private val usersCollection = db.collection("Users")

    override suspend fun loginUser(email: String, pass: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, pass).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Boolean {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun registerUser(email: String, pass: String): String? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            result.user?.uid
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveUser(user: User): Boolean {
        return try {
            usersCollection.document(user.uid).set(user).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getUserProfile(uid: String): User? {
        return try {
            val doc = usersCollection.document(uid).get().await()
            doc.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    override suspend fun uploadAvatar(uid: String, uri: Uri): String? {
        return try {
            val ref = storage.reference.child("avatars/$uid")
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun uploadAvatarData(uid: String, data: ByteArray): String? {
        return try {
            val ref = storage.reference.child("avatars/$uid")
            ref.putBytes(data).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateProfile(uid: String, updates: Map<String, Any>): Boolean {
        return try {
            usersCollection.document(uid).update(updates).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}