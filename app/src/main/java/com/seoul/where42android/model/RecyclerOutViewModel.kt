package com.seoul.where42android.model

data class RecyclerOutViewModel(var title: String, var innerList: MutableList<RecyclerInViewModel>, var groupId:Number, var viewgroup:Boolean, var comeCluster:Int)