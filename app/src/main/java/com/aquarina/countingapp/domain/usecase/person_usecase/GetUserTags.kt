package com.aquarina.countingapp.domain.usecase.person_usecase

import com.aquarina.countingapp.domain.model.UserTag
import com.aquarina.countingapp.domain.repository.PersonRepository
import kotlinx.coroutines.flow.Flow

class GetUserTags(
    private val repository: PersonRepository
) {
    operator fun invoke(): Flow<List<UserTag>> {
        return repository.getUserTags()
    }
}
