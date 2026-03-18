package com.aquarina.countingapp.presentation.features.caculating_china_poker

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquarina.countingapp.domain.model.GameInfo
import com.aquarina.countingapp.domain.model.Person
import com.aquarina.countingapp.domain.usecase.person_usecase.PersonUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

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

    private var deletedPerson: Person? = null

    private var getPersonJob: Job? = null

    private val _betLevel = mutableStateOf(1)
    val betLevel: State<Int> = _betLevel

    private val _numberOfStage = mutableStateOf(0)
    val numberOfStage: State<Int> = _numberOfStage

    var gameInfo: GameInfo? = null

    private val _initLoad = mutableStateOf(false)
//    val initload: State<Boolean> = _initLoad


    init {
        viewModelScope.launch {
//            delay(50.milliseconds)
            getGameInfo()
            getPersons()
//            _initLoad.value = true
        }
    }

    private fun getStage() {
        if (state.value.persons.isNotEmpty()) {
            _numberOfStage.value = state.value.persons.first().stages.size
        }
    }

    fun onEvent(event: PersonEvent) {
        when (event) {
            is PersonEvent.OrderPersons -> {

            }

            is PersonEvent.DeletePerson -> {
                viewModelScope.launch {
                    personUseCases.deletePerson(event.person)
                    deletedPerson = event.person
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
        }
    }

    private suspend fun getGameInfo() {
        viewModelScope.launch {
            gameInfo = personUseCases.getGameInfo()
            if (gameInfo != null) {
                _betLevel.value = gameInfo!!.betLevel
            } else {
                personUseCases.insertGameInfo(GameInfo(betLevel = 1))
                gameInfo = personUseCases.getGameInfo()
            }
        }
    }

    private fun getPersons() {
        getPersonJob?.cancel()
        getPersonJob = personUseCases.getPersons().onEach { peoples: List<Person> ->
            _state.value = state.value.copy(
                persons = peoples
            )
            getStage()
        }.launchIn(viewModelScope)
    }

    fun addPerson(name: String) {
//        if (state.value.persons.size >= 4) {
//            // TODO: show error
//            return
//        }

        val person: PersonEvent =
            PersonEvent.CreatePerson(
                person = Person(
                    name = name,
                    total = 0,
                    stages = List(size = numberOfStage.value) { 0 })
            )
        onEvent(person)
    }

    fun editPerson(name: String) {
        val person: PersonEvent =
            PersonEvent.UpdatePerson(
                person = state.value.persons[editingId.value!!].copy(name = name)

            )
        onEvent(person)
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
        if (value) {
            initName.value = state.value.persons[index].name
            Log.d("NameInput", "Tên người chơi: ${initName.value}")
            _editingId.value = index
        }
        _showDialog.value = value
    }

    fun showDialogBoxBetLevel(value: Boolean) {
        _showDialogBetLevel.value = value
    }

    fun changeBetLevel(name: String) {
        val value: Int = name.toInt()
        _betLevel.value = value
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
//        _numberOfStage.value++
    }

    fun refreshData() {
        for (person in state.value.persons) {
            onEvent(PersonEvent.UpdatePerson(person = person.copy(stages = emptyList(), total = 0)))
        }
    }

    private val _showDialogEditStage = mutableStateOf(false)
    val showDialogEditStage: State<Boolean> = _showDialogEditStage
    var listWinLose: MutableList<String> = mutableListOf()
    var stage: Int = 1
    var listWinLoseState: MutableState<List<String>> = mutableStateOf(List(4) { "" })

    fun showDialogEditStage(value: Boolean) {
        _showDialogEditStage.value = value
    }

    fun showEditStage(stage: Int) {
        this.stage = stage;
        listWinLose.clear()
        var isInit = true
        for (person in state.value.persons) {
            listWinLose.add(person.stages[stage].toString())
            if (person.stages[stage] != 0) isInit = false
        }
        if (isInit) {
            listWinLoseState.value = List(state.value.persons.size) { "" }
        } else {
            listWinLoseState.value = listWinLose
        }
        Log.d("NameInput", "stage: $listWinLose")
        _showDialogEditStage.value = true
    }

    fun updateStage() {
        for (index in 0 until listWinLoseState.value.size) {
            val listStages: MutableList<Int> = state.value.persons[index].stages.toMutableList()
            listStages[stage] = listWinLoseState.value[index].toIntOrNull() ?: 0
            onEvent(
                PersonEvent.UpdatePerson(
                    person = state.value.persons[index].copy(
                        stages = listStages,
                        total = listStages.sum()
                    )
                )
            )
        }
    }

    fun deleteStage() {
        for (person in state.value.persons) {
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
            value >= 75 -> return "🐐"
            value in 50 until 75 -> "👑"
            value in 30 until 50 -> "🥇"
            value in 15 until 30 -> "🥈"
            value in 1 until 15 -> "🥉"
            value == 0 -> return ""
            value in -14 until 0 -> "🐔"
            value in -29 until -14 -> "🤮"
            value in -49 until -29 -> "💩"
            value in -74 until -49 -> "🏦"
            else -> "⚰️"

        }
    }
}
