package com.aquarina.countingapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GameInfo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String = "Tính Tiền",
    val titleIcon: String = "Calculate",
    val betLevel: Int = 1,
    val showCurrency: Boolean = false,
    val soundConfigs: List<SoundConfig> = defaultSoundConfigs,
    val milestoneConfigs: List<MilestoneConfig> = defaultMilestoneConfigs,
    val achievementConfigs: List<AchievementConfig> = defaultAchievementConfigs,
    val selectedGameId: Int? = null
)

data class SoundConfig(
    val key: String,
    val displayName: String,
    val soundName: String,
    val duration: Long?,
    val startTime: Int = 0,
    val customUri: String? = null,
    val totalDuration: Long? = null
)

data class MilestoneConfig(
    val key: String,
    val displayName: String,
    val message: String, // Ví dụ: "Chúc mừng {name} đã đạt tới cảnh giới huyền thoại!"
    val minScore: Int? = null,
    val maxScore: Int? = null,
    val isIncrease: Boolean? = null // null: cả hai, true: chỉ khi tăng, false: chỉ khi giảm
)

data class AchievementConfig(
    val key: String,
    val name: String,
    val icon: String,
    val speakText: String,
    val minScore: Int? = null,
    val maxScore: Int? = null
)

val defaultSoundConfigs = listOf(
    SoundConfig("uprank", "Tăng hạng", "uprank", 1200L, 250, totalDuration = 2000L),
    SoundConfig("downrank", "Giảm hạng", "downrank", 1200L, 50, totalDuration = 2000L),
    SoundConfig("notchange", "Không đổi", "notchange", 1700L, 0, totalDuration = 3000L),
    SoundConfig("s5", "Trên 75 điểm", "s5", 6000L, 0, totalDuration = 10000L),
    SoundConfig("s4", "Từ 50 đến 74 điểm", "s4", null, 0, totalDuration = 10000L),
    SoundConfig("s3", "Từ 30 đến 49 điểm", "s3", 3500L, 0, totalDuration = 10000L),
    SoundConfig("s2", "Từ 15 đến 29 điểm", "s2", 1500L, 0, totalDuration = 10000L),
    SoundConfig("s1", "Từ 1 đến 14 điểm", "s1", 4000L, 0, totalDuration = 10000L),
    SoundConfig("s0", "Hòa vốn", "s0", 3500L, 0, totalDuration = 10000L),
    SoundConfig("m1", "Từ -14 đến -1 điểm", "m1", 2500L, 0, totalDuration = 10000L),
    SoundConfig("m2", "Từ -29 đến -15 điểm", "m2", 1500L, 0, totalDuration = 10000L),
    SoundConfig("m3", "Từ -49 đến -30 điểm", "m3", null, 0, totalDuration = 10000L),
    SoundConfig("m4", "Từ -74 đến -50 điểm", "m4", null, 0, totalDuration = 10000L),
    SoundConfig("m5", "Dưới -75 điểm", "m5", 3000L, 0, totalDuration = 10000L)
)

val defaultMilestoneConfigs = listOf(
    MilestoneConfig("legend", "Trên 75 điểm", "Chúc mừng {name} đã đạt tới cảnh giới huyền thoại!", minScore = 75),
    MilestoneConfig("dead", "Dưới -75 điểm", "Vĩnh biệt {name}, hòm đã sẵn sàng!", maxScore = -75),
    MilestoneConfig("whale", "Từ 50 đến 74 điểm", "{name} đã trở thành cá voi!", minScore = 50),
    MilestoneConfig("bank", "Từ -74 đến -50 điểm", "{name} đã phải tìm đến ngân hàng!", maxScore = -50),
    MilestoneConfig("king", "Từ 30 đến 49 điểm", "{name} đã chính thức lên ngôi vua!", minScore = 30),
    MilestoneConfig("shit", "Từ -49 đến -30 điểm", "Ôi không, {name} bốc cứt rồi!", maxScore = -30)
)

val defaultAchievementConfigs = listOf(
    AchievementConfig("legend", "Trên 75 điểm", "🐐", "là huyền thoại sống", minScore = 75),
    AchievementConfig("whale", "Từ 50 đến 74 điểm", "🐳", "là một con cá voi", minScore = 50, maxScore = 74),
    AchievementConfig("king", "Từ 30 đến 49 điểm", "👑", "đã lên ngôi vua", minScore = 30, maxScore = 49),
    AchievementConfig("diamond", "Từ 15 đến 29 điểm", "💎", "đạt danh hiệu kim cương", minScore = 15, maxScore = 29),
    AchievementConfig("money", "Từ 1 đến 14 điểm", "💰", "đã có túi tiền", minScore = 1, maxScore = 14),
    AchievementConfig("even", "Hòa vốn", "😐", "đang hòa vốn", minScore = 0, maxScore = 0),
    AchievementConfig("chicken", "Từ -14 đến -1 điểm", "🐔", "là con gà", minScore = -14, maxScore = -1),
    AchievementConfig("vomit", "Từ -29 đến -15 điểm", "🤮", "đang nôn mửa", minScore = -29, maxScore = -15),
    AchievementConfig("shit", "Từ -49 đến -30 điểm", "💩", "đang bốc cứt", minScore = -49, maxScore = -30),
    AchievementConfig("bank", "Từ -74 đến -50 điểm", "🏦", "đang vay ngân hàng để chơi", minScore = -74, maxScore = -50),
    AchievementConfig("dead", "Dưới -75 điểm", "⚰️", "đang nằm hòm", maxScore = -75)
)
