package com.aquarina.countingapp.presentation.features.caculating_china_poker

import android.app.Application
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
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
import com.aquarina.countingapp.presentation.components.formatToReadable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PersonsViewModel @Inject constructor(
    private val personUseCases: PersonUseCases,
    private val app: Application
) : ViewModel(), TextToSpeech.OnInitListener {

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
    
    private val _showCurrency = mutableStateOf(false)
    val showCurrency: State<Boolean> = _showCurrency

    private val _numberOfStage = mutableIntStateOf(0)
    val numberOfStage: State<Int> = _numberOfStage

    var gameInfo: GameInfo? = null

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var mediaPlayer: MediaPlayer? = null
    private var playAllJob: Job? = null
    private var updateStageJob: Job? = null
    private var ttsDeferred: CompletableDeferred<String>? = null

    init {
        viewModelScope.launch {
            getGameInfo()
            getPersons()
            getUserTags()
        }
        try {
            tts = TextToSpeech(app, this)
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    ttsDeferred?.complete(utteranceId ?: "")
                }
                override fun onError(utteranceId: String?) {
                    ttsDeferred?.complete(utteranceId ?: "")
                    Log.e("TTS", "Error speaking: $utteranceId")
                }
            })
        } catch (e: Exception) {
            Log.e("TTS", "Could not initialize TTS: ${e.message}")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("vi", "VN"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Ngôn ngữ tiếng Việt không hỗ trợ, thử tiếng Anh")
                tts?.setLanguage(Locale.US)
            }
            isTtsReady = true
        } else {
            Log.e("TTS", "Khởi tạo TTS thất bại")
            isTtsReady = false
        }
    }

    fun speak(text: String, queueMode: Int = TextToSpeech.QUEUE_ADD, utteranceId: String? = null) {
        if (isTtsReady) {
            tts?.speak(text, queueMode, null, utteranceId)
        }
    }

    fun stopPlaying() {
        playAllJob?.cancel()
        updateStageJob?.cancel()
        if (isTtsReady) tts?.stop()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        _state.value = state.value.copy(
            isProcessing = false,
            highlightedPersonId = null
        )
    }

    fun playAllPlayersInfo() {
        playAllJob?.cancel()
        playAllJob = viewModelScope.launch {
            _state.value = state.value.copy(isProcessing = true)
            try {
                if (isTtsReady) tts?.stop()
                mediaPlayer?.stop()
                state.value.persons.forEach { person ->
                    _state.value = state.value.copy(highlightedPersonId = person.id)
                    
                    val achievementText = getAchievementText(person.total)
                    val scoreText = if (showCurrency.value) {
                        val money = person.total * betLevel.value
                        if (money >= 0) "thắng ${money.formatToReadable()}" else "nợ ${kotlin.math.abs(money).formatToReadable()}"
                    } else {
                        "${person.total} điểm"
                    }
                    
                    val utteranceId = "all_info_${person.name}_${System.currentTimeMillis()}"

                    if (isTtsReady) {
                        ttsDeferred = CompletableDeferred()
                        speak(
                            "${person.name} $achievementText, $scoreText",
                            TextToSpeech.QUEUE_FLUSH,
                            utteranceId
                        )
                        try {
                            withTimeoutOrNull(5000) {
                                ttsDeferred?.await()
                            }
                        } catch (e: Exception) {
                            Log.e("TTS", "Error waiting for TTS: ${e.message}")
                        }
                    }

                    playAchievementSound(person.total)
                    delay(300) // Nghỉ một chút giữa các người chơi
                }
            } finally {
                _state.value = state.value.copy(
                    isProcessing = false,
                    highlightedPersonId = null
                )
            }
        }
    }

    private fun getAchievementText(total: Int): String {
        return when {
            total >= 75 -> "là huyền thoại sống"
            total >= 50 -> "là một con cá voi"
            total >= 30 -> "đã lên ngôi vua"
            total >= 15 -> "đạt danh hiệu kim cương"
            total >= 1 -> "đã có túi tiền"
            total == 0 -> "đang hòa vốn"
            total > -15 -> "là con gà"
            total > -30 -> "đang nôn mửa"
            total > -50 -> "đang bốc cứt"
            total > -75 -> "đang vay ngân hàng để chơi"
            else -> "đang nằm hòm"
        }
    }

    private suspend fun playAchievementSound(total: Int) {
        // Map total score to resource name string
        val rawName = when {
            total >= 75 -> "s5"
            total >= 50 -> "s4"
            total >= 30 -> "s3"
            total >= 15 -> "s2"
            total >= 1 -> "s1"
            total == 0 -> "s0"
            total > -15 -> "m1"
            total > -30 -> "m2"
            total > -50 -> "m3"
            total > -75 -> "m4"
            else -> "m5"
        }

        // --- EDIT HERE TO CHANGE TIME LIMITS (in milliseconds) ---
        val durationLimit = when (rawName) {
            "m1" -> 2500L // Play m1 for only 3 seconds
            "m2" -> 1500L
            "m3" -> null
            "m4" -> null
            "m5" -> 3000L
            "s0" -> 3500L
            "s1" -> 4000L
            "s2" -> 1500L
            "s3" -> 3500L
            "s4" -> null
            "s5" -> 6000L
            else -> null // Play other sounds fully
        }
        // ---------------------------------------------------------
        
        playSound(rawName, durationLimit)
    }

    private suspend fun playSound(rawName: String, durationLimit: Long? = null) {
        val soundResId = app.resources.getIdentifier(rawName, "raw", app.packageName)
        if (soundResId == 0) {
            Log.e("Sound", "Could not find sound resource for $rawName")
            return
        }

        val deferred = CompletableDeferred<Unit>()
        
        // Cleanup previous playback
        mediaPlayer?.stop()
        mediaPlayer?.release()
        
        mediaPlayer = MediaPlayer.create(app, soundResId)
        val player = mediaPlayer ?: run {
            deferred.complete(Unit)
            return
        }

        player.setOnCompletionListener {
            it.release()
            if (mediaPlayer == it) mediaPlayer = null
            if (!deferred.isCompleted) deferred.complete(Unit)
        }
        
        player.setOnErrorListener { mp, _, _ ->
            mp.release()
            if (mediaPlayer == mp) mediaPlayer = null
            if (!deferred.isCompleted) deferred.complete(Unit)
            true
        }

        player.start()

        // Handle time limit if set
        var timeoutJob: Job? = null
        if (durationLimit != null) {
            timeoutJob = viewModelScope.launch {
                delay(durationLimit)
                if (!deferred.isCompleted) {
                    try {
                        if (player.isPlaying) player.stop()
                    } catch (e: Exception) {
                        Log.e("Sound", "Error stopping player: ${e.message}")
                    }
                    player.release()
                    if (mediaPlayer == player) mediaPlayer = null
                    deferred.complete(Unit)
                }
            }
        }

        deferred.await()
        timeoutJob?.cancel() // Cleanup timeout job if sound finished early
    }

    private suspend fun checkMilestone(name: String, oldTotal: Int, newTotal: Int): Boolean {
        val milestoneMsg = when {
            oldTotal < 75 && newTotal >= 75 -> "Chúc mừng $name đã đạt tới cảnh giới huyền thoại!"
            oldTotal > -75 && newTotal <= -75 -> "Vĩnh biệt $name, hòm đã sẵn sàng!"
            oldTotal < 50 && newTotal >= 50 -> "$name đã trở thành cá voi!"
            oldTotal > -50 && newTotal <= -50 -> "$name đã phải tìm đến ngân hàng!"
            oldTotal < 30 && newTotal >= 30 -> "$name đã chính thức lên ngôi vua!"
            oldTotal > -30 && newTotal <= -30 -> "Ôi không, $name bốc cứt rồi!"
            else -> null
        }
        
        if (milestoneMsg != null) {
            if (isTtsReady) {
                val utteranceId = "milestone_${name}_$newTotal"
                ttsDeferred = CompletableDeferred()
                speak(milestoneMsg, TextToSpeech.QUEUE_ADD, utteranceId)
                try {
                    withTimeoutOrNull(5000) {
                        ttsDeferred?.await()
                    }
                } catch (e: Exception) {
                    Log.e("TTS", "Error waiting for milestone TTS: ${e.message}")
                }
            }
            return true
        }
        return false
    }

    fun getRankLevel(total: Int): Int {
        return when {
            total >= 75 -> 10
            total >= 50 -> 9
            total >= 30 -> 8
            total >= 15 -> 7
            total >= 1 -> 6
            total == 0 -> 5
            total > -15 -> 4
            total > -30 -> 3
            total > -50 -> 2
            total > -75 -> 1
            else -> 0
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
            _showCurrency.value = gameInfo!!.showCurrency
        } else {
            personUseCases.insertGameInfo(GameInfo(betLevel = 1, showCurrency = false))
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

    fun showDialogBoxBetLevel(value: Boolean) {
        _showDialogBetLevel.value = value
    }

    fun showDialogSelectUser(value: Boolean) {
        _showDialogSelectUser.value = value
    }

    fun updateGameSettings(betLevel: Int, showCurrency: Boolean) {
        _betLevel.intValue = betLevel
        _showCurrency.value = showCurrency
        viewModelScope.launch {
            personUseCases.updateGameInfo(
                gameInfo?.copy(betLevel = betLevel, showCurrency = showCurrency) 
                ?: GameInfo(betLevel = betLevel, showCurrency = showCurrency)
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
        _showDialogEditStage.value = false
        updateStageJob?.cancel()
        updateStageJob = viewModelScope.launch {
            // 1. Calculate all necessary updates first
            val updates = state.value.persons.mapIndexedNotNull { index, person ->
                if (index < listWinLoseState.value.size) {
                    val listStages = person.stages.toMutableList()
                    if (stage < listStages.size) {
                        val oldTotal = person.total
                        val newValue = listWinLoseState.value[index].toIntOrNull() ?: 0
                        listStages[stage] = newValue
                        val newTotal = listStages.sum()
                        val updatedPerson = person.copy(stages = listStages, total = newTotal)
                        Triple(updatedPerson, oldTotal, newTotal)
                    } else null
                } else null
            }

            if (updates.isEmpty()) return@launch

            _state.value = state.value.copy(isProcessing = true)
            try {
                // 2. Step-by-step update with synchronized sound and UI refresh
                updates.forEach { (updatedPerson, oldTotal, newTotal) ->
                    _state.value = state.value.copy(highlightedPersonId = updatedPerson.id)
                    
                    val directionSound = when {
                        newTotal > oldTotal -> "uprank"
                        newTotal < oldTotal -> "downrank"
                        else -> "notchange"
                    }

                    // SYNC: Start direction sound and UI animation simultaneously
                    // Wait for direction sound to finish before moving on
                    coroutineScope {
                        val soundJob = launch { playSound(directionSound) }
                        personUseCases.updatePerson(updatedPerson)
                        soundJob.join()
                    }

                    // Check milestone (speaks if milestone reached and waits for TTS)
                    val milestoneReached = checkMilestone(updatedPerson.name, oldTotal, newTotal)
                    
                    // Only play achievement sound if it was a milestone
                    if (milestoneReached) {
                        playAchievementSound(newTotal)
                    }
                    
                    delay(300)
                }
            } catch (e: Exception) {
                Log.e("UpdateStage", "Error updating stage: ${e.message}")
            } finally {
                // 3. ON STOP (CANCEL) OR FINISH:
                // Ensure all data is saved immediately
                withContext(NonCancellable) {
                    updates.forEach { (updatedPerson, _, _) ->
                        personUseCases.updatePerson(updatedPerson)
                    }
                    _state.value = state.value.copy(
                        isProcessing = false,
                        highlightedPersonId = null
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
            value in 50 until 75 -> "🐳"
            value in 30 until 50 -> "👑"
            value in 15 until 30 -> "💎"
            value in 1 until 15 -> "💰"
            value == 0 -> "😐"
            value in -14 until 0 -> "🐔"
            value in -29 until -14 -> "🤮"
            value in -49 until -29 -> "💩"
            value in -74 until -49 -> "🏦"
            else -> "⚰️"
        }
    }

    override fun onCleared() {
        super.onCleared()
        playAllJob?.cancel()
        updateStageJob?.cancel()
        if (isTtsReady) {
            tts?.stop()
            tts?.shutdown()
        }
        mediaPlayer?.release()
    }
}
