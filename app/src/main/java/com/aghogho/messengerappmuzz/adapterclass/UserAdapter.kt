package com.aghogho.messengerappmuzz.adapterclass

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.aghogho.messengerappmuzz.MessageChatActivity
import com.aghogho.messengerappmuzz.R
import com.aghogho.messengerappmuzz.modelclass.Users
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(
    mContext: Context,
    mUsers: List<Users>,
    isChatCheck: Boolean
): RecyclerView.Adapter<UserAdapter.SearchViewHolder?>() {

    private val mContext: Context
    private val mUsers: List<Users>
    private val isChatCheck: Boolean

    init {
        this.mContext = mContext
        this.mUsers = mUsers
        this.isChatCheck = isChatCheck
    }

    class SearchViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var userNameTxt: TextView
        var profileImageView: CircleImageView
        var onlineStatusImg: CircleImageView
        var offlineStatusImg: CircleImageView
        var lastMessageTxt: TextView

        init {
            userNameTxt = itemView.findViewById(R.id.search_username)
            profileImageView = itemView.findViewById(R.id.search_profile_image)
            onlineStatusImg = itemView.findViewById(R.id.image_online_status)
            offlineStatusImg = itemView.findViewById(R.id.image_offline_status)
            lastMessageTxt = itemView.findViewById(R.id.message_last)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val layoutView: View = LayoutInflater.from(mContext).inflate(R.layout.user_search_item_layout, parent, false)
        return UserAdapter.SearchViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val user = mUsers[position]
        holder.userNameTxt.text = user.getUserName()
        Picasso.get().load(user.getProfile()).placeholder(R.drawable.default_profile).into(holder.profileImageView)

        //specify to user what to do
        holder.itemView.setOnClickListener {
            val options = arrayOf<CharSequence>(
                "Send Message",
                "Visit Profile"
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
            builder.setTitle("What do you want?")
            builder.setItems(options, DialogInterface.OnClickListener { dialog, position ->
                if (position == 0) {
                    val intent = Intent(mContext, MessageChatActivity::class.java)
                    intent.putExtra("visit_id", user.getUID())
                    mContext.startActivity(intent)
                } else if (position == 1) {
                    //Implement Visit Profile
                }
            })
            builder.show()
        }
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

}
