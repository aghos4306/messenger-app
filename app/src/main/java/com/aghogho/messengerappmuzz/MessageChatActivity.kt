package com.aghogho.messengerappmuzz

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.aghogho.messengerappmuzz.databinding.ActivityMessageChatBinding
import com.aghogho.messengerappmuzz.modelclass.Users
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class MessageChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessageChatBinding

    private var userIdVisit: String = ""
    private var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent = intent
        userIdVisit = intent.getStringExtra("visit_id").toString()
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val reference = FirebaseDatabase.getInstance().reference.child("Users").child(userIdVisit)
        reference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val user: Users? = snapshot.getValue(Users::class.java)

                binding.usernameMchat.text = user!!.getUserName()
                Picasso.get().load(user.getProfile()).into(binding.profileImageMchat)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        binding.sendMessageBtn.setOnClickListener {
            val message = binding.textMessage.text.toString()
            if (message == "") {
                Toast.makeText(this@MessageChatActivity, "Please Specify your message", Toast.LENGTH_LONG).show()
            } else {
                sendMessageToUser(firebaseUser!!.uid, userIdVisit, message)
            }
            binding.textMessage.setText("")
        }

        binding.attachImageFileBtn.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent,"Pick Image"), 450)
        }

    }

    private fun sendMessageToUser(senderId: String, receiverId: String, message: String) {
        val reference = FirebaseDatabase.getInstance().reference
        val messageKey = reference.push().key

        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderId
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverId
        messageHashMap["isseen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messageKey
        reference.child("Chats")
            .child(messageKey!!)
            .setValue(messageHashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val chatsListReference = FirebaseDatabase.getInstance().reference.child("ChatList")
                        .child(firebaseUser!!.uid).child(userIdVisit)

                    chatsListReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (!snapshot.exists()) {
                                chatsListReference.child("id").setValue(userIdVisit)
                            }

                            val chatsListReceiverRef = FirebaseDatabase.getInstance().reference.child("ChatList")
                                .child(userIdVisit).child(firebaseUser!!.uid)
                            chatsListReceiverRef.child("id").setValue(firebaseUser!!.uid)
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })



                    val reference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 438 && resultCode == RESULT_OK && data != null && data!!.data != null) {
            val progressBar = ProgressDialog(this)
            progressBar.setMessage("Image is uploading, please wait...")
            progressBar.show()

            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storageReference.child("$messageId.jpg")

            val uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!)
            uploadTask.continueWithTask<Uri?>(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val messageHashMap = HashMap<String, Any?>()
                    messageHashMap["sender"] = firebaseUser!!.uid
                    messageHashMap["message"] = "sent you an image."
                    messageHashMap["receiver"] = userIdVisit
                    messageHashMap["isseen"] = false
                    messageHashMap["url"] = url
                    messageHashMap["messageId"] = messageId

                    ref.child("Chats").child(messageId!!).setValue(messageHashMap)

                    progressBar.dismiss()
                }
            }
        }

    }
}