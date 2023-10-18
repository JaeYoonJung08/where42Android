package com.example.where42android

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class ListViewActivity  : AppCompatActivity() {

    var profileList = arrayListOf<profile_list>(
        profile_list("Jaeyojun","handsome", "개포 c2r5s6", "profile_photo_example",),
        profile_list("Jooypark","beautiful", "개포 c2r5s6", "profile_photo_example" ),
        profile_list("jaju","graphics master", "개포 c2r5s6", "profile_photo_example"),
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_view)

        var mainListView : ListView = findViewById(R.id.peopleListView) as ListView
        val ExhibitionAdapter = ListViewAdapter(this, profileList)
        mainListView.setAdapter(ExhibitionAdapter)

        }
}