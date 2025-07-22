package com.lalo.pixdev.viewmodel.model

data class Project(
    val id: String,
    val name: String,
    val dateCreated: Long = System.currentTimeMillis(),
    val isPinned: Boolean
)