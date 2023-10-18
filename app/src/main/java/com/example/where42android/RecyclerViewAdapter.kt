package com.example.where42android

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class RecyclerViewAdapter(val context: Context, val profileList: ArrayList<profile_list>, val filterChecked: Boolean) :
    RecyclerView.Adapter<RecyclerViewAdapter.ProfileViewHolder>() {
    inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val profilePhoto: ImageView = itemView.findViewById(R.id.profile_photo)
        val intraId: TextView = itemView.findViewById(R.id.intra_id)
        val comment: TextView = itemView.findViewById(R.id.Comment)
        val locationInfo: TextView = itemView.findViewById(R.id.location_info)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_view_detail, parent, false)
        return ProfileViewHolder(view)
    }
    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        if (filterChecked) {
            // filterChecked가 true일 때만 필터링을 적용합니다.
            // '퇴근'인 요소를 건너뜁니다.
            val filteredList = profileList.filter { it.location != "퇴근" }
            val profile = filteredList[position]
            val resourceId = context.resources.getIdentifier(profile.photo, "drawable", context.packageName)
            holder.profilePhoto.setImageResource(resourceId)
            holder.intraId.text = profile.intraId
            holder.comment.text = profile.comment
            holder.locationInfo.text = profile.location
            // 여기서는 필터링된 목록을 사용합니다.
        } else {
            // filterChecked가 false이면 필터링하지 않고 원래 목록을 사용합니다.
            val profile = profileList[position]
            val resourceId = context.resources.getIdentifier(profile.photo, "drawable", context.packageName)
            holder.profilePhoto.setImageResource(resourceId)
            holder.intraId.text = profile.intraId
            holder.comment.text = profile.comment
            holder.locationInfo.text = profile.location
        }
    }
    override fun getItemCount(): Int {
        return if (filterChecked) {
            profileList.filter { it.location != "퇴근" }.size
        } else {
            profileList.size
        }
    }

}