package com.aquarina.countingapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GameSaved(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val name: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
