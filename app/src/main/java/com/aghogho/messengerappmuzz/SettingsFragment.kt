package com.aghogho.messengerappmuzz

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.aghogho.messengerappmuzz.modelclass.Users
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class SettingsFragment : Fragment() {

    private var usersReference: DatabaseReference? = null
    private var firebaseUser: FirebaseUser? = null
    private var userNameSettingsET: TextView? = null
    private var profileImgContainer: CircleImageView? = null
    private var coverImgContainer: ImageView? = null
    private var REQUEST_CODE = 450
    private var imageUri: Uri? = null
    private var uploadedStorageReference: StorageReference? = null
    private var checkIfCoverImage: String? = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        usersReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        userNameSettingsET = view.findViewById(R.id.username_settings)
        profileImgContainer = view.findViewById(R.id.profile_image_settings)
        coverImgContainer = view.findViewById(R.id.cover_image_settings)
        uploadedStorageReference = FirebaseStorage.getInstance().reference.child("User Images")

        usersReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: Users? = snapshot.getValue(Users::class.java)
                    if (context != null) {
                        userNameSettingsET!!.text = user!!.getUserName()
                        Picasso.get().load(user.getProfile()).into(profileImgContainer)
                        Picasso.get().load(user.getCover()).into(coverImgContainer)
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        view.findViewById<CircleImageView>(R.id.profile_image_settings).setOnClickListener {
            pickProfileImage()
        }

        view.findViewById<ImageView>(R.id.cover_image_settings).setOnClickListener {
            checkIfCoverImage = "cover"
            pickProfileImage()
        }

        return view

    }

    private fun pickProfileImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data!!.data != null) {
            imageUri = data.data
            Toast.makeText(context, "Uploading...", Toast.LENGTH_LONG).show()
            uploadImageToFbDatabase()
        }
    }

    private fun uploadImageToFbDatabase() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("Image is uploading, please wait...")
        progressBar.show()

        if (imageUri != null) {
            val uploadFileReff = uploadedStorageReference!!.child(System.currentTimeMillis().toString() + ".jpg")

            val uploadTask: StorageTask<*>
            uploadTask = uploadFileReff.putFile(imageUri!!)
            uploadTask.continueWithTask<Uri?>(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation uploadFileReff.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    if (checkIfCoverImage == "cover") {
                        val mapCoverImg = HashMap<String, Any>()
                        mapCoverImg["cover"]
                        usersReference!!.updateChildren(mapCoverImg)
                        checkIfCoverImage = ""
                    } else {
                        val mapProfileImg = HashMap<String, Any>()
                        mapProfileImg["profile"]
                        usersReference!!.updateChildren(mapProfileImg)
                        checkIfCoverImage = ""
                    }
                    progressBar.dismiss()
                }
            }
        }
    }

    private fun pickCoverImage() {

    }

}
