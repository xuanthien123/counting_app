package com.aquarina.countingapp.presentation.features.caculating_china_poker

import android.app.Application
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquarina.countingapp.domain.model.AchievementConfig
import com.aquarina.countingapp.domain.model.GameInfo
import com.aquarina.countingapp.domain.model.MilestoneConfig
import com.aquarina.countingapp.domain.model.Person
import com.aquarina.countingapp.domain.model.SoundConfig
import com.aquarina.countingapp.domain.model.UserTag
import com.aquarina.countingapp.domain.usecase.person_usecase.PersonUseCases
import com.aquarina.countingapp.presentation.components.formatToReadable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    private val _showDialogSettings = mutableStateOf(false)
    val showDialogSettings: State<Boolean> = _showDialogSettings

    private val _showDialogEditStage = mutableStateOf(false)
    val showDialogEditStage: State<Boolean> = _showDialogEditStage

    private val _showConfirmDialog = mutableStateOf(false)
    val showConfirmDialog: State<Boolean> = _showConfirmDialog

    private var deletedPerson: Person? = null
    private var getPersonJob: Job? = null
    private var getUserTagsJob: Job? = null

    private val _betLevel = mutableIntStateOf(1)
    val betLevel: State<Int> = _betLevel
    
    private val _showCurrency = mutableStateOf(false)
    val showCurrency: State<Boolean> = _showCurrency

    private val _title = mutableStateOf("Tính Tiền")
    val title: State<String> = _title

    private val _titleIcon = mutableStateOf("Calculate")
    val titleIcon: State<String> = _titleIcon

    private val _numberOfStage = mutableIntStateOf(0)
    val numberOfStage: State<Int> = _numberOfStage

    var gameInfo: GameInfo? = null

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var hasShownTtsError = false
    private var mediaPlayer: MediaPlayer? = null
    private var playAllJob: Job? = null
    private var updateStageJob: Job? = null
    private var ttsDeferred: CompletableDeferred<String>? = null

    val initName = mutableStateOf("")
    private val _editingId = mutableStateOf<Int?>(null)
    val editingId: State<Int?> = _editingId

    var stage: Int = 0
    val listWinLoseState = mutableStateOf<List<String>>(emptyList())

    var titleConfirm: String = "Xác nhận"
    var contentConfirm: String = ""
    var functionConfirm: () -> Unit = {}

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
            showTtsError("Thiết bị không thể khởi tạo dịch vụ giọng nói (TTS)")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("vi", "VN"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.US)
            }
            isTtsReady = true
        } else {
            isTtsReady = false
            showTtsError("Lỗi khởi tạo giọng nói.")
        }
    }

    private fun showTtsError(message: String) {
        if (!hasShownTtsError) {
            Toast.makeText(app, message, Toast.LENGTH_LONG).show()
            hasShownTtsError = true
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
        _state.value = state.value.copy(isProcessing = false, highlightedPersonId = null)
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
                        speak("${person.name} $achievementText, $scoreText", TextToSpeech.QUEUE_FLUSH, utteranceId)
                        withTimeoutOrNull(5000) { ttsDeferred?.await() }
                    }

                    playAchievementSound(person.total)
                    delay(300) 
                }
            } finally {
                _state.value = state.value.copy(isProcessing = false, highlightedPersonId = null)
            }
        }
    }

    private fun getAchievementText(total: Int): String {
        val configs = state.value.gameInfo?.achievementConfigs
        if (!configs.isNullOrEmpty()) {
            val matchingConfig = configs
                .filter { cfg ->
                    val matchMin = cfg.minScore?.let { total >= it } ?: true
                    val matchMax = cfg.maxScore?.let { total <= it } ?: true
                    matchMin && matchMax
                }
                .sortedByDescending { it.minScore ?: Int.MIN_VALUE }
                .firstOrNull()
            
            if (matchingConfig != null) return matchingConfig.speakText
        }

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

    private fun getSoundConfig(key: String): SoundConfig? {
        val configs = state.value.gameInfo?.soundConfigs ?: gameInfo?.soundConfigs
        return configs?.find { it.key == key }
    }

    private suspend fun playAchievementSound(total: Int) {
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

        val config = getSoundConfig(rawName)
        playSound(rawName, config?.duration, config?.startTime)
    }

    private suspend fun playSound(rawName: String, durationLimit: Long? = null, startTime: Int? = null) {
        val soundResId = app.resources.getIdentifier(rawName, "raw", app.packageName)
        if (soundResId == 0) return

        val deferred = CompletableDeferred<Unit>()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        
        mediaPlayer = MediaPlayer.create(app, soundResId)
        val player = mediaPlayer ?: return

        player.setOnCompletionListener {
            it.release()
            if (mediaPlayer == it) mediaPlayer = null
            deferred.complete(Unit)
        }
        
        player.setOnErrorListener { mp, _, _ ->
            mp.release()
            if (mediaPlayer == mp) mediaPlayer = null
            deferred.complete(Unit)
            true
        }

        startTime?.let { if (it > 0) player.seekTo(it) }
        player.start()

        var timeoutJob: Job? = null
        if (durationLimit != null) {
            timeoutJob = viewModelScope.launch {
                delay(durationLimit)
                if (!deferred.isCompleted) {
                    try { if (player.isPlaying) player.stop() } catch (e: Exception) {}
                    player.release()
                    if (mediaPlayer == player) mediaPlayer = null
                    deferred.complete(Unit)
                }
            }
        }

        deferred.await()
        timeoutJob?.cancel() 
    }

    private suspend fun checkMilestone(name: String, oldTotal: Int, newTotal: Int): Boolean {
        val milestoneConfigs = (state.value.gameInfo?.milestoneConfigs ?: gameInfo?.milestoneConfigs) ?: emptyList()
        val config = milestoneConfigs.find { cfg ->
            val matchMin = cfg.minScore?.let { oldTotal < it && newTotal >= it } ?: false
            val matchMax = cfg.maxScore?.let { oldTotal > it && newTotal <= it } ?: false
            matchMin || matchMax
        } ?: return false

        val message = config.message.replace("{name}", name)
        if (isTtsReady) {
            val utteranceId = "milestone_${name}_${System.currentTimeMillis()}"
            ttsDeferred = CompletableDeferred()
            speak(message, TextToSpeech.QUEUE_ADD, utteranceId)
            withTimeoutOrNull(5000) { ttsDeferred?.await() }
        }
        return true
    }

    fun onEvent(event: PersonEvent) {
        when (event) {
            is PersonEvent.OrderPersons -> {}
            is PersonEvent.DeletePerson -> {
                viewModelScope.launch {
                    personUseCases.deletePerson(event.person)
                    deletedPerson = event.person
                }
            }
            is PersonEvent.DeleteAllPerson -> {
                viewModelScope.launch { personUseCases.deleteAllPerson() }
            }
            is PersonEvent.RestorePerson -> {
                viewModelScope.launch {
                    personUseCases.insertPerson(deletedPerson ?: return@launch)
                    deletedPerson = null
                }
            }
            is PersonEvent.UpdatePerson -> {
                viewModelScope.launch { personUseCases.updatePerson(event.person) }
            }
            is PersonEvent.CreatePerson -> {
                viewModelScope.launch { personUseCases.insertPerson(event.person) }
            }
            is PersonEvent.CreateUserTag -> {
                viewModelScope.launch { personUseCases.insertUserTag(UserTag(name = event.name)) }
            }
            is PersonEvent.DeleteUserTag -> {
                viewModelScope.launch { personUseCases.deleteUserTag(event.userTag) }
            }
            is PersonEvent.ToggleTagSelection -> {
                val currentSelected = state.value.selectedTagIds
                val newSelected = if (currentSelected.contains(event.tagId)) currentSelected - event.tagId else currentSelected + event.tagId
                _state.value = state.value.copy(selectedTagIds = newSelected)
            }
            is PersonEvent.AddSelectedTagsToGame -> {
                val selectedTags = state.value.userTags.filter { it.id in state.value.selectedTagIds }
                selectedTags.forEach { addPerson(it.name) }
            }
        }
    }

    private suspend fun getGameInfo() {
        gameInfo = personUseCases.getGameInfo()
        if (gameInfo != null) {
            _betLevel.intValue = gameInfo!!.betLevel
            _showCurrency.value = gameInfo!!.showCurrency
            _title.value = gameInfo!!.title
            _titleIcon.value = gameInfo!!.titleIcon
            _state.value = state.value.copy(gameInfo = gameInfo)
        } else {
            val defaultGameInfo = GameInfo(betLevel = 1, showCurrency = false)
            personUseCases.insertGameInfo(defaultGameInfo)
            gameInfo = personUseCases.getGameInfo()
            _state.value = state.value.copy(gameInfo = gameInfo)
        }
    }

    private fun getPersons() {
        getPersonJob?.cancel()
        getPersonJob = personUseCases.getPersons().onEach { peoples ->
            _state.value = state.value.copy(persons = peoples)
            _numberOfStage.intValue = peoples.firstOrNull()?.stages?.size ?: 0
        }.launchIn(viewModelScope)
    }

    private fun getUserTags() {
        getUserTagsJob?.cancel()
        getUserTagsJob = personUseCases.getUserTags().onEach { tags ->
            _state.value = state.value.copy(userTags = tags)
        }.launchIn(viewModelScope)
    }

    fun addUserTag(name: String) = onEvent(PersonEvent.CreateUserTag(name))
    fun deleteUserTag(userTag: UserTag) = onEvent(PersonEvent.DeleteUserTag(userTag))
    fun toggleTagSelection(tagId: Int) = onEvent(PersonEvent.ToggleTagSelection(tagId))
    fun addSelectedTagsAsPersons() = onEvent(PersonEvent.AddSelectedTagsToGame)

    fun addPerson(name: String) {
        onEvent(PersonEvent.CreatePerson(Person(name = name, total = 0, stages = List(numberOfStage.value) { 0 })))
    }

    fun editPerson(name: String) {
        editingId.value?.let { index ->
            if (index in state.value.persons.indices) {
                onEvent(PersonEvent.UpdatePerson(state.value.persons[index].copy(name = name)))
            }
        }
    }

    fun deleteAllPerson() {
        onEvent(PersonEvent.DeleteAllPerson)
        _numberOfStage.intValue = 0
    }

    fun showDialogBox(value: Boolean) {
        if (value) {
            initName.value = ""
            _editingId.value = null
        }
        _showDialog.value = value
    }

    fun showEditName(value: Boolean, index: Int) {
        if (value && index in state.value.persons.indices) {
            initName.value = state.value.persons[index].name
            _editingId.value = index
            _showDialog.value = true
        } else if (!value) {
            _showDialog.value = false
        }
    }

    fun showDialogBoxBetLevel(value: Boolean) { _showDialogBetLevel.value = value }
    fun showDialogSelectUser(value: Boolean) { _showDialogSelectUser.value = value }
    fun showDialogSettings(value: Boolean) { _showDialogSettings.value = value }

    fun updateGameSettings(betLevel: Int, showCurrency: Boolean) {
        updateGameSettingsFull(
            soundConfigs = gameInfo?.soundConfigs ?: emptyList(),
            milestoneConfigs = gameInfo?.milestoneConfigs ?: emptyList(),
            achievementConfigs = gameInfo?.achievementConfigs ?: emptyList(),
            betLevel = betLevel,
            showCurrency = showCurrency
        )
    }

    fun updateGameSettingsFull(
        soundConfigs: List<SoundConfig>,
        milestoneConfigs: List<MilestoneConfig>,
        achievementConfigs: List<AchievementConfig>? = null,
        title: String? = null,
        titleIcon: String? = null,
        betLevel: Int? = null,
        showCurrency: Boolean? = null
    ) {
        viewModelScope.launch {
            var updated = gameInfo?.copy(soundConfigs = soundConfigs, milestoneConfigs = milestoneConfigs)
                ?: GameInfo(soundConfigs = soundConfigs, milestoneConfigs = milestoneConfigs)

            achievementConfigs?.let { updated = updated.copy(achievementConfigs = it) }
            title?.let { updated = updated.copy(title = it); _title.value = it }
            titleIcon?.let { updated = updated.copy(titleIcon = it); _titleIcon.value = it }
            betLevel?.let { updated = updated.copy(betLevel = it); _betLevel.intValue = it }
            showCurrency?.let { updated = updated.copy(showCurrency = it); _showCurrency.value = it }

            personUseCases.updateGameInfo(updated)
            gameInfo = updated
            _state.value = state.value.copy(gameInfo = updated)
        }
    }

    fun playSoundPreview(config: SoundConfig) {
        viewModelScope.launch { playSound(config.soundName, config.duration, config.startTime) }
    }

    fun speakPreview(name: String, message: String) {
        if (isTtsReady) {
            speak(message.replace("{name}", name), TextToSpeech.QUEUE_ADD, "preview")
        }
    }

    fun addNewStage() {
        viewModelScope.launch {
            state.value.persons.forEach { person ->
                personUseCases.updatePerson(person.copy(stages = person.stages + 0))
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            state.value.persons.forEach { person ->
                personUseCases.updatePerson(person.copy(stages = emptyList(), total = 0))
            }
        }
    }

    fun showDialogEditStage(value: Boolean) { _showDialogEditStage.value = value }

    fun showEditStage(stage: Int) {
        this.stage = stage
        val currentList = state.value.persons.map { person ->
            person.stages.getOrNull(stage)?.toString() ?: "0"
        }
        val isAllZero = currentList.all { it == "0" || it == "" }
        listWinLoseState.value = if (isAllZero) List(state.value.persons.size) { "" } else currentList
        _showDialogEditStage.value = true
    }

    fun updateStage() {
        _showDialogEditStage.value = false
        updateStageJob?.cancel()
        updateStageJob = viewModelScope.launch {
            val updates = state.value.persons.mapIndexedNotNull { index, person ->
                if (index < listWinLoseState.value.size) {
                    val listStages = person.stages.toMutableList()
                    if (stage < listStages.size) {
                        val oldTotal = person.total
                        val newValue = listWinLoseState.value[index].toIntOrNull() ?: 0
                        listStages[stage] = newValue
                        val newTotal = listStages.sum()
                        Triple(person.copy(stages = listStages, total = newTotal), oldTotal, newTotal)
                    } else null
                } else null
            }

            if (updates.isEmpty()) return@launch

            _state.value = state.value.copy(isProcessing = true)
            try {
                updates.forEach { (updatedPerson, oldTotal, newTotal) ->
                    _state.value = state.value.copy(highlightedPersonId = updatedPerson.id)
                    val directionSound = when {
                        newTotal > oldTotal -> "uprank"
                        newTotal < oldTotal -> "downrank"
                        else -> "notchange"
                    }
                    val config = getSoundConfig(directionSound)
                    coroutineScope {
                        val soundJob = launch { playSound(directionSound, config?.duration, config?.startTime) }
                        personUseCases.updatePerson(updatedPerson)
                        soundJob.join()
                    }
                    if (checkMilestone(updatedPerson.name, oldTotal, newTotal)) {
                        playAchievementSound(newTotal)
                    }
                    delay(300)
                }
            } catch (e: Exception) {
                Log.e("UpdateStage", "Error updating stage: ${e.message}")
            } finally {
                _state.value = state.value.copy(isProcessing = false, highlightedPersonId = null)
            }
        }
    }

    fun deleteStage() {
        viewModelScope.launch {
            state.value.persons.forEach { person ->
                if (stage < person.stages.size) {
                    val listStages = person.stages.toMutableList()
                    listStages.removeAt(stage)
                    personUseCases.updatePerson(person.copy(stages = listStages, total = listStages.sum()))
                }
            }
        }
    }

    fun deleteUser(user: Person) {
        viewModelScope.launch {
            state.value.persons.forEach { person ->
                val total = person.stages.sum()
                personUseCases.updatePerson(person.copy(stages = listOf(total), total = total))
            }
            personUseCases.deletePerson(user)
            _showDialog.value = false
        }
    }

    fun closeConfirmDialog() { _showConfirmDialog.value = false }

    fun showConfirmDialog(title: String, content: String, function: () -> Unit) {
        this.titleConfirm = title
        this.contentConfirm = content
        this.functionConfirm = function
        _showConfirmDialog.value = true
    }

    fun getAchievement(total: Int): String {
        val configs = state.value.gameInfo?.achievementConfigs
        if (!configs.isNullOrEmpty()) {
            val matchingConfig = configs
                .filter { cfg ->
                    val matchMin = cfg.minScore?.let { total >= it } ?: true
                    val matchMax = cfg.maxScore?.let { total <= it } ?: true
                    matchMin && matchMax
                }
                .sortedByDescending { it.minScore ?: Int.MIN_VALUE }
                .firstOrNull()

            if (matchingConfig != null) return matchingConfig.icon
        }

        return when {
            total >= 75 -> "🐐"
            total in 50 until 75 -> "🐳"
            total in 30 until 50 -> "👑"
            total in 15 until 30 -> "💎"
            total in 1 until 15 -> "💰"
            total == 0 -> "😐"
            total in -14 until 0 -> "🐔"
            total in -29 until -14 -> "🤮"
            total in -49 until -29 -> "💩"
            total in -74 until -49 -> "🏦"
            else -> "⚰️"
        }
    }

    override fun onCleared() {
        super.onCleared()
        playAllJob?.cancel()
        updateStageJob?.cancel()
        tts?.stop()
        tts?.shutdown()
        mediaPlayer?.release()
    }
}
