package com.aquarina.countingapp.presentation.features.caculating_china_poker

import com.aquarina.countingapp.domain.model.GameSaved
import com.aquarina.countingapp.domain.model.Person
import com.aquarina.countingapp.domain.model.UserTag

sealed class PersonEvent {
    object OrderPersons : PersonEvent()
    data class UpdatePerson(val person: Person) : PersonEvent()
    data class DeletePerson(val person: Person) : PersonEvent()
    object RestorePerson : PersonEvent()
    object Undo : PersonEvent()
    object Redo : PersonEvent()
    data class CreatePerson(val person: Person) : PersonEvent()
    object DeleteAllPerson : PersonEvent()

    data class CreateUserTag(val name: String) : PersonEvent()
    data class DeleteUserTag(val userTag: UserTag) : PersonEvent()
    data class ToggleTagSelection(val tagId: Int) : PersonEvent()
    object AddSelectedTagsToGame : PersonEvent()
    object ClearTagSelection : PersonEvent()

    data class UpdateSoundConfig(val key: String, val uri: String) : PersonEvent()
    data class ResetSoundConfig(val key: String) : PersonEvent()

    // GameSaved events
    data class CreateGameSaved(val name: String?) : PersonEvent()
    data class DeleteGameSaved(val game: GameSaved) : PersonEvent()
    data class DeleteMultipleGameSaved(val games: List<GameSaved>) : PersonEvent()
    data class UpdateGameSaved(val game: GameSaved) : PersonEvent()
    data class SelectGame(val gameId: Int) : PersonEvent()
}
