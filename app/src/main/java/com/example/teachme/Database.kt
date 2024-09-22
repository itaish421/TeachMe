package com.example.teachme

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.teachme.models.ChatRoom
import com.example.teachme.models.LessonRequest
import com.example.teachme.models.LessonRequestStatus
import com.example.teachme.models.Message
import com.example.teachme.models.RegisterForm
import com.example.teachme.models.Student
import com.example.teachme.models.Teacher
import com.example.teachme.models.User
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirestoreRegistrar
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
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

    fun deleteAllRequests() {
        FirebaseFirestore.getInstance()
            .collection("users")
            .get()
            .addOnSuccessListener {
                it.forEach {  doc ->
                    doc.reference.update("incomingRequests", listOf<LessonRequest>())
                    doc.reference.update("outgoingRequests", listOf<LessonRequest>())
                }
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

    fun listenUsers(liveData: MutableLiveData<List<User>>): ListenerRegistration {

        return FirebaseFirestore.getInstance()
            .collection("users")
            .addSnapshotListener {value, err ->
                val users = mutableListOf<User>()

                value?.forEach {  doc->
                    if(doc.getBoolean("teacher") ?: false) {
                        users.add(doc.toObject(Teacher::class.java))
                    }
                    else {
                        users.add(doc.toObject(Student::class.java))
                    }
                }
                liveData.postValue(users)
            }
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


    fun sendMessageToChat(chatRoom: ChatRoom, sender: String, content: String) {
        val message = Message(
            sender = sender,
            content = content
        )
        chatRoom.messages.add(message)
        FirebaseDatabase.getInstance()
            .getReference("chats")
            .child(chatRoom.id)
            .setValue(chatRoom)
    }

    fun listenToChat(chatId: String, callback: (ChatRoom) -> Unit): ValueEventListener {
        return FirebaseDatabase.getInstance()
            .getReference("chats")
            .child(chatId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val room = snapshot.getValue(ChatRoom::class.java) ?: return
                    callback.invoke(room)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private suspend fun hasChatWithTeacher(teacher: Teacher, student: Student) =
        withContext(Dispatchers.IO) {
            val value = CompletableDeferred<ChatRoom?>()

            FirebaseDatabase.getInstance().getReference("chats")
                .get()
                .addOnSuccessListener {
                    if (it.exists() && it.childrenCount > 0) {
                        it.children.mapNotNull { roomData ->
                            roomData.getValue(ChatRoom::class.java)
                        }.firstOrNull { room ->
                            room.teacherId == teacher.id && room.studentId == student.id
                        }.let { room ->
                            value.complete(
                                room
                            )
                        }
                    } else {
                        value.complete(null)
                    }
                }
                .addOnFailureListener {
                    value.complete(null)
                }
            value.await()
        }

    suspend fun startChatWithTeacher(teacher: Teacher, student: Student) =
        withContext(Dispatchers.IO) {
            val value = CompletableDeferred<ChatRoom>()
            val chatRoomExisting = hasChatWithTeacher(teacher, student)
            chatRoomExisting?.let {
                value.complete(it)
            } ?: run {
                val newDoc = FirebaseDatabase.getInstance().getReference("chats").push()
                val newRoom = ChatRoom(
                    id = newDoc.key!!,
                    studentId = student.id,
                    teacherId = teacher.id,
                    messages = mutableListOf()
                )
                newDoc.setValue(newRoom)
                    .addOnSuccessListener {
                        value.complete(newRoom)
                    }
                    .addOnFailureListener {
                        value.completeExceptionally(it)
                    }
            }
            value.await()
        }


    @Throws(Exception::class)
    suspend fun getChats() = withContext(Dispatchers.IO) {
        val value = CompletableDeferred<List<ChatRoom>>()
        val currentUserId = FirebaseAuth.getInstance().uid!!
        val baseQuery: Query = FirebaseDatabase.getInstance().getReference("chats")

        baseQuery.get()
            .addOnSuccessListener {
                val rooms = it.children.mapNotNull { room -> room.getValue(ChatRoom::class.java) }
                    .filter { room -> room.studentId == currentUserId || room.teacherId == currentUserId }
                value.complete(rooms)
            }
            .addOnFailureListener {
                value.completeExceptionally(it)
            }
        value.await()
    }

    suspend fun rateTeacher(
        teacher: Teacher,
        student: Student,
        rating: Double,
    ) = withContext(Dispatchers.IO) {
        val value = CompletableDeferred<Double>()
        teacher.ratings.add(rating)
        student.ratedTeachers.add(teacher.id)

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(teacher.id)
            .set(teacher)
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(student.id)
            .set(student)
            .addOnSuccessListener {
                value.complete(rating)
            }
            .addOnFailureListener {
                value.completeExceptionally(it)
            }
        value.await()
    }

    suspend fun scheduleLessonRequest(
        teacher: Teacher,
        request: LessonRequest,
    ) = withContext(Dispatchers.IO) {
        val value = CompletableDeferred<LessonRequest>()
        val newDoc = FirebaseFirestore
            .getInstance()
            .collection("requests")
            .document()
        request.id = newDoc.id
        newDoc.set(request)
            .addOnSuccessListener {
                value.complete(request)
                teacher.incomingRequests.add(request)
                // add the request to the teacher's doc
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(teacher.id)
                    .set(teacher)

            }
            .addOnFailureListener {
                value.completeExceptionally(it)
            }
        value.await()
    }

    suspend fun changeRequestStatus(
        teacher: Teacher,
        request: LessonRequest,
        status: LessonRequestStatus,
    ) =
        withContext(Dispatchers.IO) {
            val value = CompletableDeferred<LessonRequest>()
            request.status = status
            val req = teacher.incomingRequests.find {
                it.id == request.id
            }
            req?.status = status
            FirebaseFirestore.getInstance()
                .collection("requests")
                .document(request.id)
                .set(request)
                .addOnSuccessListener {
                    value.complete(request)
                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(teacher.id)
                        .set(teacher)
                }
                .addOnFailureListener {
                    value.completeExceptionally(it)
                }
            value.await()
        }

    fun listenMyRequests(
        user: User,
        liveData: MutableLiveData<List<LessonRequest>>,
    ): ListenerRegistration {
        return FirebaseFirestore.getInstance()
            .collection("requests")
            .whereEqualTo(
                if (user is Teacher) {
                    "teacherId"
                } else {
                    "studentId"
                }, user.id
            )
            .addSnapshotListener { snap, _ ->
                snap?.toObjects(LessonRequest::class.java)?.let {
                    liveData.postValue(it)
                }
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


    suspend fun updateUser(user: User, newName:String, newImage: Uri?) = withContext(Dispatchers.IO) {
        val value = CompletableDeferred<User>()

        val imageUrl = newImage?.let {
            uploadImage(it, "userImages/${user.id}")
        } ?: user.image
        FirebaseFirestore.getInstance().collection("users")
            .document(user.id)
            .update("image", imageUrl, "fullName", newName)
            .addOnSuccessListener {
                user.image = imageUrl
                user.fullName = newName
                value.complete(user)
            }
            .addOnFailureListener {
                value.completeExceptionally(it)
            }

        value.await()

    }
    suspend fun register(form: RegisterForm, coroutineScope: CoroutineScope): User =
        withContext(Dispatchers.IO) {
            val value = CompletableDeferred<User>()

            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(form.email, form.password)
                .addOnSuccessListener { authResult ->
                    // create Document for user in database
                    coroutineScope.launch(Dispatchers.IO) {
                        try {
                            val imageUrl = form.image?.let {
                                uploadImage(it, "userImages/${authResult.user!!.uid}")
                            }
                                ?: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcROgY9BG36B1t7RZCr_i18RcjgfSJTFyUx0-w&s"

                            if (form.teacher) {
                                val details = form.teacherDetails!!

                                val teacher = Teacher(
                                    id = authResult.user!!.uid,
                                    fullName = form.fullName,
                                    email = form.email,
                                    phone = form.phone,
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
            value.await()
        }
}