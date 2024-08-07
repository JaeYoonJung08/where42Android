package com.seoul.where42android.LiveData

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seoul.where42android.Base_url_api_Retrofit.CommentChangeMember
import com.seoul.where42android.Base_url_api_Retrofit.Member
import com.seoul.where42android.Base_url_api_Retrofit.MemberAPI
import com.seoul.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.seoul.where42android.Base_url_api_Retrofit.UpdateCommentRequest
import com.seoul.where42android.Base_url_api_Retrofit.locationCustomMemberRequest
import com.seoul.where42android.Base_url_api_Retrofit.locationCustomMemberResponse
import com.seoul.where42android.Base_url_api_Retrofit.member_custom_location
import com.seoul.where42android.Base_url_api_Retrofit.reissueAPI
import com.seoul.where42android.R
import com.seoul.where42android.main.MainPageActivity
import com.seoul.where42android.main.UserSettings
import com.seoul.where42android.main.friendListObject
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//의존성 주입(Dependency Injection) 라이브러리를 사용하거나, 직접 싱글톤 패턴을 구현할 수 있습니다.
// 여기에는 Kotlin의 object 키워드를 활용하여 싱글톤 객체를 만드는 방법이 있습니다.
class ProfileList private constructor(): ViewModel() {


    private val profile = MutableLiveData<Member?>()
    val profileLiveData: LiveData<Member?>
        get() = profile


    val usersetting = UserSettings.getInstance()

    // 싱글톤으로 사용할 객체 선언
    companion object {
        @Volatile
        private var instance: ProfileList? = null

        fun getInstance(): ProfileList {
            return instance ?: synchronized(this) {
                instance ?: ProfileList().also { instance = it }
            }
        }
    }

    fun getMemberData(intraId: Int, token : String, context:Context) : Boolean{
        val retrofitAPI = RetrofitConnection.getInstance(token).create(MemberAPI::class.java)
        viewModelScope.launch {
            try {
                val response = retrofitAPI.getMember()
//                val response = retrofitAPI.getMember(intraId)
                if (response.isSuccessful) {
                    val member: Member? = response.body()
                    member?.let {
                        member.responsecode = 3
                        if (it.location == null)
                        {
                            if (it.inCluster == true)
                            {
                                usersetting.inCluster = true
                                it.location = "개포 클러스터 내"
                            }
                            else
                            {
                                usersetting.inCluster = false
                                it.location = "퇴근"
                            }
                        }
                        else
                        {
                            usersetting.inCluster = true
                        }
                        friendListObject.addItem(it.intraId, it.intraName)
                        profile.value = it
                        Log.e("ProfileList", "${profile.value}")
                    } ?: run {
                        profile.value = Member(
                            intraId = -1,
                            intraName = "",
                            grade = "",
                            image = "",
                            comment = "",
                            inCluster = false,
                            agree = false,
                            defaultGroupId = -1,
                            location = "",
                            responsecode = 4
                        )
                        Log.d("ProfileList", "${profile.value}")
                    }
                }
                else
                {
                    // Handle unsuccessful response
                    profile.value = null
                    Log.d("ProfileList", "error1")

                    //reissue api 부르기
                    try {
                        Log.e("Reissue_SUC", "refreshtoken : ${usersetting.refreshToken}")
                        val reissueapi = RetrofitConnection.getInstance(usersetting.refreshToken).create(
                            reissueAPI::class.java)
                        val reissueResponse = reissueapi.reissueToken()
                        if (reissueResponse.isSuccessful)
                        {
                            when (reissueResponse.code())
                            {
                                200 -> {
                                    Log.e("Reissue_SUC", "reissueResponse : ${reissueResponse}")
                                    Log.e("Reissue_SUC", "reissueResponse : ${reissueResponse.body()}")
                                    Log.e("Reissue_SUC", "reissueResponse : ${reissueResponse.code()}")
                                    val reissue = reissueResponse.body()?.refreshToken
                                    Log.e("Reissue_SUC", "reissue : ${reissue}")
                                    if (reissue != null)
                                    {
                                        usersetting.token = reissue
                                        profile.value = Member(
                                            intraId = -1,
                                            intraName = "",
                                            grade = "",
                                            image = "",
                                            comment = "",
                                            inCluster = false,
                                            agree = false,
                                            defaultGroupId = -1,
                                            location = "",
                                            responsecode = 200
                                        )
//                                        saveaccesTokenToSharedPreferences(this@MainActivity, reissue)
                                        Log.d("token_check", "here6")
                                        Log.d("SUC", "SUC ${response.code()}")

                                    }
                                    else
                                    {
                                        usersetting.token = "notoken"
//                                        saveaccesTokenToSharedPreferences(this@MainActivity, "notoken")
                                    }

                                }
                                // 추가적인 상태 코드에 대한 처리 필요
                                else ->
                                {
                                    Log.e("Reissue_SUC", "reissueResponse fail : ${reissueResponse}")
                                    Log.e("Reissue_SUC", "reissueResponse fail: ${reissueResponse.code()}")
                                    // 기본적으로 어떻게 처리할지 작성
                                }
                            }
                        } else {

                            val reissueapi = RetrofitConnection.getInstance(usersetting.refreshToken).create(reissueAPI::class.java)
                            val reissueResponse = reissueapi.reissueToken()
                            when (response.code())
                            {
                                //성공
                                200 -> {
                                    usersetting.token = reissueResponse.toString()
                                    //화면이 새로고침
                                    (context as Activity).recreate()
                                }
                                //401이며 Reissue API 호출 실패 시 처리 리다이렉트로 보내야함.
                                401 -> {
                                    //다시 로그인 해달라는 걸 띄우고 MainActivity로 보내자.
                                    val dialog = Dialog(context)
                                    dialog.setContentView(R.layout.activity_editstatus_popup)

                                    dialog.setCanceledOnTouchOutside(true)
                                    dialog.setCancelable(true)
                                    dialog.window?.setGravity(Gravity.CENTER)
                                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


                                    val editText = dialog.findViewById<TextView>(R.id.title)
                                    editText.text = "다시 로그인을 해주세요"

                                    val btnCancel = dialog.findViewById<Button>(R.id.cancel)
                                    btnCancel.visibility = View.GONE
                                    val btnSubmit = dialog.findViewById<Button>(R.id.submit)
                                    btnSubmit.setOnClickListener {
                                        //MainActivity로 보내야됨.
//                                        위 코드에서 getLaunchIntentForPackage() 함수는 현재 앱의 런처 액티비티의 Intent를 가져옵니다. 이후에 addFlags() 함수를 사용하여 새로운 태스크를 시작하고, 기존의 태스크를 제거하는 플래그를 추가합니다. 그리고 마지막으로 startActivity() 함수를 호출하여 앱을 다시 시작합니다.
                                        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                                        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        context.startActivity(intent)

                                        dialog.dismiss()
                                    }
                                    dialog.show()
                                }
                            }


                        }
                    } catch (reissueException: Exception) {
                    }
                }
            } catch (e: Exception) {
                profile.value = null // Setting null in case of network failure or exceptions
                Log.d("ProfileList", "error2")
                Log.d("ProfileList", "error : ${e.message}")
                e.printStackTrace()
            }
        }
        if (profile.value == null)
            return true
        else
            return false
    }

    fun updateMemberComment(updateCommentRequest: UpdateCommentRequest, token: String) {
        Log.d("ProfileList", "updateMemberComment : ${profile.value}")
        val retrofitAPI = RetrofitConnection.getInstance(token).create(MemberAPI::class.java)

        retrofitAPI.updateMemberComment(updateCommentRequest).enqueue(object :
            Callback<CommentChangeMember> {
            override fun onResponse(
                call: Call<CommentChangeMember>,
                response: Response<CommentChangeMember>
            ) {
                if (response.isSuccessful) {
                    val commentChangeMember: CommentChangeMember? = response.body()
                    val currentProfile = profile.value
                    Log.d("ProfileList", "${profile.value}")
                    val updatedComment = commentChangeMember?.comment ?: ""
                    Log.e("checkpoint", "1currentProfile -> ${profile.value}, commentChangeMember -> ${commentChangeMember}")
                    currentProfile?.let {
                        Log.e("checkpoint", "2currentProfile -> ${profile.value}, commentChangeMember -> ${commentChangeMember}")
                        val updatedProfile = it.copy(comment = updatedComment, location = it.location ?: "")
                        Log.e("checkpoint", "3currentProfile -> ${profile.value}, commentChangeMember -> ${commentChangeMember}")
                        profile.postValue(updatedProfile) // Update LiveData with new value
                    } ?: run {
                        // Handle the case when profile.value is null
                        val newProfile = Member(
                            intraId = -1,
                            intraName = "",
                            grade = "",
                            image = "",
                            comment = updatedComment,
                            inCluster = false,
                            agree = false,
                            defaultGroupId = -1,
                            location = "",
                            responsecode = -1
                        )
                        profile.postValue(newProfile)
                    }
                    // 추가: 성공 응답 로그
                    Log.d("ProfileList", "onResponse: Success")
                } else {
                    // 추가: 실패 응답 로그
                    Log.e("ProfileList", "onResponse: Failure")
                }
            }

            override fun onFailure(call: Call<CommentChangeMember>, t: Throwable) {
                Log.e("respone2 fail", "fail")
                // Handle failure
                // For seoul, handle the failure accordingly
            }
        })
    }

        private val editlocationcustom = MutableLiveData<locationCustomMemberResponse>()
        val editlocationcustomLiveData: LiveData<locationCustomMemberResponse>
        get() = editlocationcustom

        fun updateMemberCustomLocaton(locationCustomMemberRequest: locationCustomMemberRequest, token: String) {
//            val retrofitAPI =
//                RetrofitConnection_data.getInstance().create(member_custom_location::class.java)
            val retrofitAPI =
                RetrofitConnection.getInstance(token).create(member_custom_location::class.java)
            val call = retrofitAPI.customLocationChange(locationCustomMemberRequest)

            call.enqueue(object : Callback<locationCustomMemberResponse> {
                override fun onResponse(
                    call: Call<locationCustomMemberResponse>,
                    response: Response<locationCustomMemberResponse>
                ) {
                    if (response.isSuccessful) {
                        val newGroupResponse = response.body()
                        Log.e(
                            "DELETE_Suc",
                            "Sucess to editcustomlocation. code: ${response.code()}"
                        )

                        newGroupResponse?.let { response ->
                            Log.d("DELETE_Suc", "newGroupResponse : ${newGroupResponse}")
                            Log.d("DELETE_Suc", "profile.value  : ${profile.value}")
                            var profileValue = profile.value
                            profileValue?.location = response.customLocation
                            profile.value = profileValue
                            Log.d("DELETE_Suc", "profile.value_fin  : ${profile.value}")
                        }
                        // 성공적으로 삭제되었으므로 적절한 처리를 수행합니다.
                    } else {
                        // API 호출에 실패한 경우
                        Log.e(
                            "DELETE_ERROR",
                            "Sucess to editcustomlocation. code: ${response.code()}"
                        )

                    }
                }

                override fun onFailure(call: Call<locationCustomMemberResponse>, t: Throwable) {

                    Log.e("CREATE_ERROR", "Network error occurred. Message: ${t.message}")
                }
            })


        }

    }

















//fun getAllMembers() {
//    retrofitAPI.getMembers().enqueue(object : Callback<List<Member>> {
//        override fun onResponse(call: Call<List<Member>>, response: Response<List<Member>>) {
//            if (response.isSuccessful) {
//                // Handle successful response
//                val members: List<Member>? = response.body()
//                // Process the list of members as needed
//            } else {
//                // Handle unsuccessful response
//                // Handle unsuccessful response
//                // For seoul, setting an empty list
//                val emptyList: List<Member> = emptyList()
//                // Process empty list or handle the error accordingly
//            }
//        }
//
//        override fun onFailure(call: Call<List<Member>>, t: Throwable) {
//            // Handle failure
//            // For seoul, setting an empty list
//            val emptyList: List<Member> = emptyList()
//            // Process empty list or handle the failure accordingly
//        }
//    })
//}
//
//fun updateMemberComment(updateCommentRequest: UpdateCommentRequest) {
//    retrofitAPI.updateMemberComment(updateCommentRequest).enqueue(object : Callback<CommentChangeMember> {
//        override fun onResponse(call: Call<CommentChangeMember>, response: Response<CommentChangeMember>) {
//            if (response.isSuccessful) {
//                // Handle successful response
//                val commentChangeMember: CommentChangeMember? = response.body()
//                // Process the updated member comment as needed
//            } else {
//                // Handle unsuccessful response
//                // For seoul, handle the error accordingly
//            }
//        }
//
//        override fun onFailure(call: Call<CommentChangeMember>, t: Throwable) {
//            // Handle failure
//            // For seoul, handle the failure accordingly
//        }
//    })