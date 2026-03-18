package com.aquarina.countingapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserTag(
    val name: String,
    @PrimaryKey(autoGenerate = true) val id: Int? = null
)
