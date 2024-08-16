package com.example.teachme

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.teachme.models.RegisterForm
import com.example.teachme.models.Student
import com.example.teachme.models.Teacher
import com.example.teachme.models.User
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.Exception

data object Database {

    val userLiveData = MutableLiveData<Resource<User>>(Resource(data = null))
    private var userDocumentListenerRegistration: ListenerRegistration? = null
    private val authStateListener = FirebaseAuth.AuthStateListener { authState ->
        if (authState.currentUser != null) {
            userDocumentListenerRegistration = FirebaseFirestore.getInstance()
                .collection("users")
                .document(authState.uid!!)
                .addSnapshotListener { value, _ ->
                    Log.d("Value", "Value changed")
                    val isTeacher = value?.getBoolean("teacher") ?: false
                    if (isTeacher) {
                        value?.toObject(Teacher::class.java)?.let {
                            userLiveData.postValue(
                                Resource(
                                    loaded = true,
                                    data = it
                                )
                            )
                        }
                    } else {
                        value?.toObject(Student::class.java)?.let {
                            userLiveData.postValue(
                                Resource(
                                    loaded = true,
                                    data = it
                                )
                            )
                        }
                    }
                }
        } else {
            userLiveData.postValue(Resource(loaded = true, data = null))
            userDocumentListenerRegistration?.remove()
        }
    }


    fun startListening() {
        userDocumentListenerRegistration?.remove()
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    }

    fun stopListening() {
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
        userDocumentListenerRegistration?.remove()
    }


    fun getTeachersBySubject(
        subject: String,
        teachersLiveData: MutableLiveData<List<Teacher>>,
        exceptionLiveData: MutableLiveData<Exception>,
    ) {

        FirebaseFirestore.getInstance()
            .collection("users")
            .whereEqualTo("teacher", true)
            .get()
            .addOnSuccessListener { snapshot ->
                var allTeachers = snapshot.documents.mapNotNull { it.toObject(Teacher::class.java) }
                if (subject != "all") {
                    allTeachers =
                        allTeachers.filter { it.teacherDetails.subjects.contains(subject) }
                }
                teachersLiveData.postValue(allTeachers)
            }
            .addOnFailureListener {
                exceptionLiveData.postValue(it)
            }

    }

    private suspend fun uploadImage(uri: Uri, path: String): String = withContext(Dispatchers.IO) {
        val value = CompletableDeferred<String>()
        val ref = FirebaseStorage.getInstance().getReference(path)
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { imageUrl ->
                    value.complete(imageUrl.toString())
                }.addOnFailureListener {
                    value.completeExceptionally(it)
                }
            }
            .addOnFailureListener {
                value.completeExceptionally(it)
            }
        value.await()
    }


    fun logOut() {
        FirebaseAuth.getInstance().signOut()
    }

    suspend fun login(email: String, password: String) = withContext(Dispatchers.IO) {
        val value = CompletableDeferred<AuthResult>()
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                value.complete(it)
            }
            .addOnFailureListener {
                value.completeExceptionally(it)
            }
        value.await()
    }

    suspend fun register(form: RegisterForm, coroutineScope: CoroutineScope) {
        val value = CompletableDeferred<User>()

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(form.email, form.password)
            .addOnSuccessListener { authResult ->
                // create Document for user in database
                coroutineScope.launch(Dispatchers.IO) {
                    try {
                        val imageUrl = uploadImage(form.image, "userImages/")
                        if (form.teacher) {
                            val details = form.teacherDetails!!

                            val teacher = Teacher(
                                id = authResult.user!!.uid,
                                fullName = form.fullName,
                                email = form.email,
                                image = imageUrl,
                                teacherDetails = details
                            )

                            FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(authResult.user!!.uid)
                                .set(teacher)
                                .addOnSuccessListener {
                                    value.complete(teacher)
                                }
                                .addOnFailureListener {
                                    value.completeExceptionally(it)
                                }
                        } else { // student
                            val student = Student(
                                id = authResult.user!!.uid,
                                fullName = form.fullName,
                                email = form.email,
                                image = imageUrl,
                            )
                            FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(authResult.user!!.uid)
                                .set(student)
                                .addOnSuccessListener {
                                    value.complete(student)
                                }
                                .addOnFailureListener {
                                    value.completeExceptionally(it)
                                }
                        }
                    } catch (ex: Exception) {
                        value.completeExceptionally(ex)
                    }
                }
            }
            .addOnFailureListener {
                value.completeExceptionally(it)
            }
    }
}