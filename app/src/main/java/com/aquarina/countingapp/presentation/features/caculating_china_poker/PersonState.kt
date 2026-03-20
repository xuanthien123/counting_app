package com.aquarina.countingapp.presentation.features.caculating_china_poker

import com.aquarina.countingapp.domain.model.Person
import com.aquarina.countingapp.domain.model.UserTag

data class PersonState(
    val persons: List<Person> = emptyList(),
    val userTags: List<UserTag> = emptyList(),
    val selectedTagIds: Set<Int> = emptySet(),
    val isProcessing: Boolean = false,
    val highlightedPersonId: Int? = null
)
