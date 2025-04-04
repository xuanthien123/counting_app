package com.aquarina.countingapp.presentation.features.caculating_china_poker

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquarina.countingapp.domain.model.GameInfo
import com.aquarina.countingapp.domain.model.Person
import com.aquarina.countingapp.domain.usecase.PersonUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
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

    private var deletedPerson: Person? = null

    private var getPersonJob: Job? = null

    private val _betLevel = mutableStateOf(0)
    val betLevel: State<Int> = _betLevel

    private val _numberOfStage = mutableStateOf(0)
    val numberOfStage: State<Int> = _numberOfStage

    var gameInfo: GameInfo? = null

    init {
        viewModelScope.launch {
            getGameInfo()
            getPersons()
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
                personUseCases.insertGameInfo(GameInfo(betLevel = 0))
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
        if (state.value.persons.size >= 4) {
            // TODO: show error
            return
        }
        val person: PersonEvent =
            PersonEvent.CreatePerson(
                person = Person(
                    name = name,
                    total = 0,
                    stages = List(size = numberOfStage.value) { 0 })
            )
        onEvent(person)
    }

    fun deleteAllPerson() {
        onEvent(PersonEvent.DeleteAllPerson)
        _numberOfStage.value = 0
    }

    fun showDialogBox(value: Boolean) {
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
            onEvent(PersonEvent.UpdatePerson(person = person.copy(stages = emptyList())))
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
        for (person in state.value.persons) {
            listWinLose.add(person.stages[stage].toString())
        }
        listWinLoseState.value = listWinLose
        Log.d("NameInput", "stage: $listWinLose")
        _showDialogEditStage.value = true
    }

    fun updateStage() {
        for (index in 0 until listWinLoseState.value.size) {
            val listStages: MutableList<Int> = state.value.persons[index].stages.toMutableList()
            listStages[stage] = listWinLoseState.value[index].toIntOrNull() ?: 0
            Log.d("NameInput", "stage: ${listStages[stage]}")
            Log.d("NameInput", "stage: ${state.value.persons[index].copy(stages = listStages)}")
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

}
