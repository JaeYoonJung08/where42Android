package com.example.where42android.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.where42android.Base_url_api_Retrofit.friendGroup_default_memberlist
import com.example.where42android.R
import de.hdodenhof.circleimageview.CircleImageView

class RecyclerViewAdapter_defaultList(
    private val context: Context,
    private val dataList: List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>,
    private val isEditable: Boolean
) : RecyclerView.Adapter<RecyclerViewAdapter_defaultList.ViewHolder>() {

    //checkBox
    // 각 항목의 체크 여부를 저장하기 위한 리스트
    val checkedItems = mutableListOf<friendGroup_default_memberlist.friendGroup_default_memberlistItem>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_create_group_list_view_detail_checkbox, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.bind(currentItem)

        // checkbox 항목이 클릭되었을 때의 처리
        holder.itemView.setOnClickListener {
            // 체크된 항목이면 리스트에서 제거하고, 그렇지 않으면 추가합니다.
            if (currentItem in checkedItems) {
                checkedItems.remove(currentItem)
            } else {
                checkedItems.add(currentItem)
            }
            // TODO: 체크된 항목 상태에 따라 UI 업데이트 로직 추가

            // 변경된 상태를 알리고 UI를 업데이트할 수 있도록 notifyDataSetChanged() 등을 호출합니다.
            notifyDataSetChanged()
        }
        // TODO: 기존의 onBindViewHolder() 코드 작성은 여기에 해당합니다.
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewName: CircleImageView = itemView.findViewById(R.id.profile_photo)
        private val textViewGrade: TextView = itemView.findViewById(R.id.intra_id)
        private val textViewLocation: TextView = itemView.findViewById(R.id.Comment)
        private val textViewComment: TextView = itemView.findViewById(R.id.location_info)

        fun bind(member: friendGroup_default_memberlist.friendGroup_default_memberlistItem) {
            Glide.with(context)
                .load(member.image) // Assuming 'member.image' is the URL or path to the image
                .placeholder(R.drawable.placeholder) // Placeholder image while loading
                .error(R.drawable.placeholder) // Error image if Glide fails to load
                .into(textViewName)

            textViewGrade.text = member.memberIntraName
            textViewLocation.text = member.comment
            textViewComment.text = member.location ?: "No comment available"

            val checkBox: CheckBox = itemView.findViewById(R.id.checkBox) // 체크박스 ID에 맞게 수정
            checkBox.isChecked = member in checkedItems

            // 체크박스 클릭 이벤트 처리
            checkBox.setOnClickListener {
                // 체크된 항목이면 리스트에서 제거하고, 그렇지 않으면 추가합니다.
                if (member in checkedItems) {
                    checkedItems.remove(member)
                } else {
                    checkedItems.add(member)
                }
                // 변경된 상태를 알리고 UI를 업데이트할 수 있도록 notifyDataSetChanged() 등을 호출합니다.
                notifyDataSetChanged()
            }
        }
    }
}