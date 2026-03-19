package com.aquarina.countingapp.presentation.features.caculating_china_poker

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquarina.countingapp.domain.model.GameInfo
import com.aquarina.countingapp.domain.model.Person
import com.aquarina.countingapp.domain.model.UserTag
import com.aquarina.countingapp.domain.usecase.person_usecase.PersonUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonsViewModel @Inject constructor(
    private val personUseCases: PersonUseCases
) : ViewModel() {

    private val _state = mutableStateOf(PersonState())
    val state: State<PersonState> = _state

    private val _showDialog = mutableStateOf(false)
    val showDialog: State<Boolean> = _showDialog

    private val _showDialogBetLevel = mutableStateOf(false)
    val showDialogBetLevel: State<Boolean> = _showDialogBetLevel

    private val _showDialogSelectUser = mutableStateOf(false)
    val showDialogSelectUser: State<Boolean> = _showDialogSelectUser

    private var deletedPerson: Person? = null

    private var getPersonJob: Job? = null
    private var getUserTagsJob: Job? = null

    private val _betLevel = mutableIntStateOf(1)
    val betLevel: State<Int> = _betLevel

    private val _numberOfStage = mutableIntStateOf(0)
    val numberOfStage: State<Int> = _numberOfStage

    var gameInfo: GameInfo? = null

    init {
        viewModelScope.launch {
            getGameInfo()
            getPersons()
            getUserTags()
        }
    }

    private fun getStage() {
        if (state.value.persons.isNotEmpty()) {
            _numberOfStage.intValue = state.value.persons.first().stages.size
        } else {
            _numberOfStage.intValue = 0
        }
    }

    fun onEvent(event: PersonEvent) {
        when (event) {
            is PersonEvent.OrderPersons -> {
                // Not implemented
            }

            is PersonEvent.DeletePerson -> {
                viewModelScope.launch {
                    personUseCases.deletePerson(event.person)
                    deletedPerson = event.person
                    getPersons()
                }
            }

            is PersonEvent.DeleteAllPerson -> {
                viewModelScope.launch {
                    personUseCases.deleteAllPerson()
                }
            }

            is PersonEvent.RestorePerson -> {
                viewModelScope.launch {
                    personUseCases.insertPerson(deletedPerson ?: return@launch)
                    deletedPerson = null
                }
            }

            is PersonEvent.UpdatePerson -> {
                viewModelScope.launch {
                    personUseCases.updatePerson(person = event.person)
                }
            }

            is PersonEvent.CreatePerson -> {
                viewModelScope.launch {
                    personUseCases.insertPerson(event.person)
                }
            }

            is PersonEvent.CreateUserTag -> {
                viewModelScope.launch {
                    personUseCases.insertUserTag(UserTag(name = event.name))
                }
            }

            is PersonEvent.DeleteUserTag -> {
                viewModelScope.launch {
                    personUseCases.deleteUserTag(event.userTag)
                }
            }

            is PersonEvent.ToggleTagSelection -> {
                val currentSelected = state.value.selectedTagIds
                val newSelected = if (currentSelected.contains(event.tagId)) {
                    currentSelected - event.tagId
                } else {
                    currentSelected + event.tagId
                }
                _state.value = state.value.copy(selectedTagIds = newSelected)
            }

            is PersonEvent.AddSelectedTagsToGame -> {
                val selectedTags = state.value.userTags.filter { it.id in state.value.selectedTagIds }
                selectedTags.forEach { tag ->
                    addPerson(tag.name)
                }
                _state.value = state.value.copy(selectedTagIds = emptySet())
                showDialogSelectUser(false)
            }
        }
    }

    private suspend fun getGameInfo() {
        gameInfo = personUseCases.getGameInfo()
        if (gameInfo != null) {
            _betLevel.intValue = gameInfo!!.betLevel
        } else {
            personUseCases.insertGameInfo(GameInfo(betLevel = 1))
            gameInfo = personUseCases.getGameInfo()
        }
    }

    private fun getPersons() {
        getPersonJob?.cancel()
        getPersonJob = personUseCases.getPersons().onEach { peoples ->
            _state.value = state.value.copy(
                persons = peoples
            )
            getStage()
        }.launchIn(viewModelScope)
    }

    private fun getUserTags() {
        getUserTagsJob?.cancel()
        getUserTagsJob = personUseCases.getUserTags().onEach { tags ->
            _state.value = state.value.copy(
                userTags = tags
            )
        }.launchIn(viewModelScope)
    }

    fun addUserTag(name: String) {
        onEvent(PersonEvent.CreateUserTag(name))
    }

    fun deleteUserTag(userTag: UserTag) {
        onEvent(PersonEvent.DeleteUserTag(userTag))
    }

    fun toggleTagSelection(tagId: Int) {
        onEvent(PersonEvent.ToggleTagSelection(tagId))
    }

    fun addSelectedTagsAsPersons() {
        onEvent(PersonEvent.AddSelectedTagsToGame)
    }

    fun addPerson(name: String) {
        onEvent(
            PersonEvent.CreatePerson(
                person = Person(
                    name = name,
                    total = 0,
                    stages = List(size = numberOfStage.value) { 0 })
            )
        )
    }

    fun editPerson(name: String) {
        val index = editingId.value
        if (index != null && index >= 0 && index < state.value.persons.size) {
            val person: PersonEvent =
                PersonEvent.UpdatePerson(
                    person = state.value.persons[index].copy(name = name)
                )
            onEvent(person)
        }
    }

    fun deleteAllPerson() {
        onEvent(PersonEvent.DeleteAllPerson)
        _numberOfStage.value = 0
    }

    val initName = mutableStateOf("")
    private val _editingId: MutableState<Int?> = mutableStateOf(null)
    val editingId: State<Int?> = _editingId

    fun showDialogBox(value: Boolean) {
        if (value) {
            initName.value = ""
            _editingId.value = null
        }
        _showDialog.value = value
    }

    fun showEditName(value: Boolean, index: Int) {
        if (value && index >= 0 && index < state.value.persons.size) {
            initName.value = state.value.persons[index].name
            Log.d("NameInput", "Tên người chơi: ${initName.value}")
            _editingId.value = index
            _showDialog.value = true
        } else if (!value) {
            _showDialog.value = false
        }
    }

//    fun showDialogBox(value: Boolean) {
//        _showDialog.value = value
//    }

    fun showDialogBoxBetLevel(value: Boolean) {
        _showDialogBetLevel.value = value
    }

    fun showDialogSelectUser(value: Boolean) {
        _showDialogSelectUser.value = value
    }

    fun changeBetLevel(name: String) {
        val value = name.toIntOrNull() ?: 0
        _betLevel.intValue = value
        viewModelScope.launch {
            personUseCases.updateGameInfo(
                gameInfo?.copy(betLevel = value) ?: GameInfo(betLevel = value)
            )
        }
    }

    fun addNewStage() {
        for (person in state.value.persons) {
            onEvent(PersonEvent.UpdatePerson(person = person.copy(stages = (person.stages + 0))))
        }
    }

    fun refreshData() {
        for (person in state.value.persons) {
            onEvent(PersonEvent.UpdatePerson(person = person.copy(stages = emptyList(), total = 0)))
        }
    }

    private val _showDialogEditStage = mutableStateOf(false)
    val showDialogEditStage: State<Boolean> = _showDialogEditStage
    var stage: Int = 0
    val listWinLoseState = mutableStateOf<List<String>>(emptyList())

    fun showDialogEditStage(value: Boolean) {
        _showDialogEditStage.value = value
    }

    fun showEditStage(stage: Int) {
        this.stage = stage
        val currentList = mutableListOf<String>()
        var isInit = true
        for (person in state.value.persons) {
            if (person.stages.size > stage) {
                val valStr = person.stages[stage].toString()
                currentList.add(valStr)
                if (person.stages[stage] != 0) isInit = false
            } else {
                currentList.add("0")
            }
        }
        if (isInit) {
            listWinLoseState.value = List(state.value.persons.size) { "" }
        } else {
            listWinLoseState.value = currentList.toList()
        }
        _showDialogEditStage.value = true
    }

    fun updateStage() {
        for (index in 0 until listWinLoseState.value.size) {
            if (index < state.value.persons.size) {
                val person = state.value.persons[index]
                val listStages: MutableList<Int> = person.stages.toMutableList()
                if (stage < listStages.size) {
                    listStages[stage] = listWinLoseState.value[index].toIntOrNull() ?: 0
                    onEvent(
                        PersonEvent.UpdatePerson(
                            person = person.copy(
                                stages = listStages,
                                total = listStages.sum()
                            )
                        )
                    )
                }
            }
        }
    }

    fun deleteStage() {
        for (person in state.value.persons) {
            if (stage < person.stages.size) {
                val listStages: MutableList<Int> = person.stages.toMutableList()
                listStages.removeAt(stage)
                onEvent(
                    PersonEvent.UpdatePerson(
                        person = person.copy(
                            stages = listStages,
                            total = listStages.sum()
                        )
                    )
                )
            }
        }
    }

    fun deleteUser(user: Person) {
        for (person in state.value.persons) {
            val total: Int = person.stages.sum()
            onEvent(
                PersonEvent.UpdatePerson(
                    person = person.copy(
                        stages = listOf(total),
                        total = total
                    )
                )
            )
        }
        onEvent(PersonEvent.DeletePerson(user))
        _showDialog.value = false
    }

    private val _showConfirmDialog = mutableStateOf(false)
    val showConfirmDialog: State<Boolean> = _showConfirmDialog
    var title: String = "Xác nhận"
    var content: String = ""
    var function: () -> Unit = {}
    fun closeConfirmDialog() {
        _showConfirmDialog.value = false
    }

    fun showConfirmDialog(title: String, content: String, function: () -> Unit) {
        this.title = title
        this.content = content
        this.function = function
        _showConfirmDialog.value = true
    }

    fun getAchievement(value: Int): String {
        return when {
            value >= 75 -> "🐐"
            value in 50 until 75 -> "👑"
            value in 30 until 50 -> "🥇"
            value in 15 until 30 -> "🥈"
            value in 1 until 15 -> "🥉"
            value == 0 -> ""
            value in -14 until 0 -> "🐔"
            value in -29 until -14 -> "🤮"
            value in -49 until -29 -> "💩"
            value in -74 until -49 -> "🏦"
            else -> "⚰️"
        }
    }
}
