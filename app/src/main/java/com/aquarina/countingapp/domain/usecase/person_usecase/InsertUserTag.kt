package com.aquarina.countingapp.domain.usecase.person_usecase

import com.aquarina.countingapp.domain.model.UserTag
import com.aquarina.countingapp.domain.repository.PersonRepository

class InsertUserTag(
    private val repository: PersonRepository
) {
    suspend operator fun invoke(userTag: UserTag) {
        repository.insertUserTag(userTag)
    }
}
