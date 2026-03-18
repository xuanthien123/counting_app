package com.aquarina.countingapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class GameInfo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val betLevel: Int = 0
)
