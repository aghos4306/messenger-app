package com.aghogho.messengerappmuzz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.aghogho.messengerappmuzz.modelclass.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class SettingsFragment : Fragment() {

    private var usersReference: DatabaseReference? = null
    private var firebaseUser: FirebaseUser? = null
    private var userNameSettingsET: TextView? = null
    private var profileImgContainer: CircleImageView? = null
    private var coverImgContainer: ImageView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        usersReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        userNameSettingsET = view.findViewById(R.id.username_settings)
        profileImgContainer = view.findViewById(R.id.profile_image_settings)
        coverImgContainer = view.findViewById(R.id.cover_image_settings)

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

        return view
    }

}
