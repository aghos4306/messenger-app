package com.aghogho.messengerappmuzz.adapterclass

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aghogho.messengerappmuzz.R
import com.aghogho.messengerappmuzz.modelclass.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatsAdapter(
    mContext: Context,
    mChatList: List<Chat>,
    imageUrl: String
): RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder?>() {

    private var mContext: Context
    private var mChatList: List<Chat>
    private var imageUrl: String

    private var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    init {
        this.mContext = mContext
        this.mChatList = mChatList
        this.imageUrl = imageUrl
    }

    inner class ChatsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var profileImg: CircleImageView? = null
        var showTextMsg: TextView? = null
        var leftImageView: ImageView? = null
        var textSeen: TextView? = null
        var rightImageView: ImageView? = null

        init {
            profileImg = itemView.findViewById(R.id.profile_image_chat)
            showTextMsg = itemView.findViewById(R.id.show_text_message)
            leftImageView = itemView.findViewById(R.id.left_image_view)
            textSeen = itemView.findViewById(R.id.text_seen)
            rightImageView = itemView.findViewById(R.id.right_image_view)
        }
    }

    //position 0 is messageItemRight, position 1 is messageItemLeft
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ChatsViewHolder {
        return if (position == 0) {
            val view: View = LayoutInflater.from(mContext).inflate(R.layout.message_item_right, parent, false)
            ChatsViewHolder(view)
        } else {
            val view: View = LayoutInflater.from(mContext).inflate(R.layout.message_item_left, parent, false)
            ChatsViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        val chat: Chat = mChatList[position]

        Picasso.get().load(imageUrl).into(holder.profileImg)

        if (chat.getSender().equals("sent you an image.") && !chat.getUrl().equals("")) {

            //image message - right side
            if (chat.getSender().equals(firebaseUser!!.uid)) {
                holder.showTextMsg!!.visibility = View.GONE
                holder.rightImageView!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.rightImageView)
            }
            //image message - left side
            else if (!chat.getSender().equals(firebaseUser!!.uid)) {
                holder.showTextMsg!!.visibility = View.GONE
                holder.leftImageView!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.leftImageView)
            }
        } else {
            holder.showTextMsg!!.text = chat.getMessage()
        }
        //Sent and seen msg
        if (position == mChatList.size - 1) {

           if (chat.isIsSeen()) {
               holder.textSeen!!.text = "Seen"
               if (chat.getSender().equals("sent you an image.") && !chat.getUrl().equals("")) {
                   val lp: RelativeLayout.LayoutParams? = holder.textSeen!!.layoutParams as RelativeLayout.LayoutParams
                   lp!!.setMargins(0, 245, 10, 0)
                   holder.textSeen!!.layoutParams = lp
               }
           } else {
               holder.textSeen!!.text = "Sent"
               if (chat.getSender().equals("sent you an image.") && !chat.getUrl().equals("")) {
                   val lp: RelativeLayout.LayoutParams? = holder.textSeen!!.layoutParams as RelativeLayout.LayoutParams
                   lp!!.setMargins(0, 245, 10, 0)
                   holder.textSeen!!.layoutParams = lp
               }
           }

        } else {
            holder.textSeen!!.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return mChatList!!.size
    }

    override fun getItemViewType(position: Int): Int {

        return if (mChatList[position].getSender().equals(firebaseUser!!.uid)) {
            1
        } else {
            0
        }
    }

}
