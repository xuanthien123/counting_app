package com.aquarina.countingapp.presentation.features.caculating_china_poker.component

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.aquarina.countingapp.presentation.features.caculating_china_poker.PersonsViewModel
import com.aquarina.countingapp.domain.model.SoundConfig
import com.aquarina.countingapp.domain.model.MilestoneConfig
import com.aquarina.countingapp.domain.model.AchievementConfig
import com.aquarina.countingapp.presentation.features.caculating_china_poker.availableIcons
import com.aquarina.countingapp.presentation.features.caculating_china_poker.getIconByName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogSettings(
    viewModel: PersonsViewModel,
    onDismiss: () -> Unit
) {
    val gameInfo = viewModel.gameInfo ?: return
    var selectedTab by remember { mutableIntStateOf(0) }
    
    var soundConfigs by remember { mutableStateOf(gameInfo.soundConfigs) }
    var milestoneConfigs by remember { mutableStateOf(gameInfo.milestoneConfigs) }
    var achievementConfigs by remember { mutableStateOf(gameInfo.achievementConfigs) }
    var title by remember { mutableStateOf(viewModel.title.value) }
    var titleIcon by remember { mutableStateOf(viewModel.titleIcon.value) }
    var betLevel by remember { mutableIntStateOf(viewModel.betLevel.value) }
    var showCurrency by remember { mutableStateOf(viewModel.showCurrency.value) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Cài đặt", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text("Tùy chỉnh trải nghiệm của bạn", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Trở về")
                            }
                        },
                        actions = {
                            Button(
                                onClick = {
                                    viewModel.updateGameSettingsFull(
                                        soundConfigs = soundConfigs,
                                        milestoneConfigs = milestoneConfigs,
                                        achievementConfigs = achievementConfigs,
                                        title = title,
                                        titleIcon = titleIcon,
                                        betLevel = betLevel,
                                        showCurrency = showCurrency
                                    )
                                    onDismiss()
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Icon(Icons.Default.Save, null, modifier = Modifier.size(18.dp))

                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            scrolledContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            ) { padding ->
                Column(modifier = Modifier.padding(padding)) {
                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        edgePadding = 16.dp,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        divider = {}
                    ) {
                        TabItem(0, "Chung", Icons.Default.Settings, selectedTab) { selectedTab = 0 }
                        TabItem(1, "Âm thanh", Icons.Default.MusicNote, selectedTab) { selectedTab = 1 }
                        TabItem(2, "Lời thoại", Icons.Default.RecordVoiceOver, selectedTab) { selectedTab = 2 }
                        TabItem(3, "Thành tựu", Icons.Default.EmojiEvents, selectedTab) { selectedTab = 3 }
                    }

                    Box(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                        AnimatedContent(
                            targetState = selectedTab,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            }, label = "tab_content"
                        ) { target ->
                            when (target) {
                                0 -> GeneralSettings(
                                    title = title,
                                    onTitleChange = { title = it },
                                    selectedIcon = titleIcon,
                                    onIconSelect = { titleIcon = it },
                                    betLevel = betLevel,
                                    onBetLevelChange = { betLevel = it },
                                    showCurrency = showCurrency,
                                    onShowCurrencyChange = { showCurrency = it }
                                )
                                1 -> SoundSettingsList(
                                    configs = soundConfigs,
                                    onConfigChange = { soundConfigs = it },
                                    onPreview = { viewModel.playSoundPreview(it) }
                                )
                                2 -> MilestoneSettingsList(
                                    configs = milestoneConfigs,
                                    onConfigChange = { milestoneConfigs = it },
                                    onPreview = { name, msg -> viewModel.speakPreview(name, msg) }
                                )
                                3 -> AchievementSettingsList(
                                    configs = achievementConfigs,
                                    onConfigChange = { achievementConfigs = it },
                                    onPreview = { name, msg -> viewModel.speakPreview(name, msg) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TabItem(index: Int, label: String, icon: ImageVector, selectedIndex: Int, onClick: () -> Unit) {
    Tab(
        selected = selectedIndex == index,
        onClick = onClick,
        text = {
            Text(
                label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal
            )
        },
        icon = {
            Icon(
                icon,
                null,
                modifier = Modifier.size(20.dp),
                tint = if (selectedIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}

@Composable
fun SettingSection(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
            Icon(icon, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun GeneralSettings(
    title: String,
    onTitleChange: (String) -> Unit,
    selectedIcon: String,
    onIconSelect: (String) -> Unit,
    betLevel: Int,
    onBetLevelChange: (Int) -> Unit,
    showCurrency: Boolean,
    onShowCurrencyChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingSection("Giao diện ứng dụng", Icons.Default.Palette) {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Tiêu đề ứng dụng") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp)) }
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text("Chọn biểu tượng hiển thị", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 56.dp),
                modifier = Modifier.heightIn(max = 240.dp),
                contentPadding = PaddingValues(2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(availableIcons) { iconName ->
                    val icon = getIconByName(iconName)
                    val isSelected = iconName == selectedIcon

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onIconSelect(iconName) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = iconName,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        SettingSection("Cấu hình mức cược", Icons.Default.Payments) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Hiển thị tiền tệ", fontWeight = FontWeight.Medium)
                    Text("Hiển thị đơn vị $ khi tính toán", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = showCurrency,
                    onCheckedChange = onShowCurrencyChange
                )
            }

            OutlinedTextField(
                value = betLevel.toString(),
                onValueChange = { onBetLevelChange(it.toIntOrNull() ?: 0) },
                label = { Text("Mức cược") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                prefix = { Text("₫ ", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) },
                supportingText = { Text("Số tiền quy đổi cho 1 điểm") }
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun SoundSettingsList(
    configs: List<SoundConfig>,
    onConfigChange: (List<SoundConfig>) -> Unit,
    onPreview: (SoundConfig) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(configs) { config ->
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.SettingsVoice, null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(config.displayName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.weight(1f))
                        FilledTonalIconButton(
                            onClick = { onPreview(config) },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, null)
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = (config.duration ?: "").toString(),
                            onValueChange = { newVal ->
                                val updated = configs.map { 
                                    if (it.key == config.key) it.copy(duration = newVal.toLongOrNull()) else it 
                                }
                                onConfigChange(updated)
                            },
                            label = { Text("Thời lượng") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            suffix = { Text("ms", style = MaterialTheme.typography.labelSmall) }
                        )
                        OutlinedTextField(
                            value = config.startTime.toString(),
                            onValueChange = { newVal ->
                                val updated = configs.map { 
                                    if (it.key == config.key) it.copy(startTime = newVal.toIntOrNull() ?: 0) else it 
                                }
                                onConfigChange(updated)
                            },
                            label = { Text("Bắt đầu") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            suffix = { Text("ms", style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MilestoneSettingsList(
    configs: List<MilestoneConfig>,
    onConfigChange: (List<MilestoneConfig>) -> Unit,
    onPreview: (String, String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(configs) { config ->
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.ChatBubbleOutline, null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(config.displayName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.weight(1f))
                        IconButton(
                            onClick = { onPreview("Người chơi", config.message) },
                            colors = IconButtonDefaults.filledTonalIconButtonColors()
                        ) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, null)
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = config.message,
                        onValueChange = { newVal ->
                            val updated = configs.map { 
                                if (it.key == config.key) it.copy(message = newVal) else it 
                            }
                            onConfigChange(updated)
                        },
                        label = { Text("Câu thông báo (Dùng {name} cho tên)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AchievementSettingsList(
    configs: List<AchievementConfig>,
    onConfigChange: (List<AchievementConfig>) -> Unit,
    onPreview: (String, String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(configs) { config ->
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            config.icon,
                            modifier = Modifier
                                .size(40.dp)
                                .background(MaterialTheme.colorScheme.tertiaryContainer, CircleShape)
                                .wrapContentSize(Alignment.Center),
                            fontSize = 24.sp
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(config.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.weight(1f))
                        IconButton(
                            onClick = { onPreview("Người chơi", config.speakText) },
                            colors = IconButtonDefaults.filledTonalIconButtonColors()
                        ) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, null)
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = config.icon,
                            onValueChange = { newVal ->
                                val updated = configs.map { 
                                    if (it.key == config.key) it.copy(icon = newVal) else it 
                                }
                                onConfigChange(updated)
                            },
                            label = { Text("Emoji") },
                            modifier = Modifier.width(80.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontSize = 20.sp)
                        )
                        OutlinedTextField(
                            value = config.speakText,
                            onValueChange = { newVal ->
                                val updated = configs.map { 
                                    if (it.key == config.key) it.copy(speakText = newVal) else it 
                                }
                                onConfigChange(updated)
                            },
                            label = { Text("Lời đọc (Ví dụ: là huyền thoại sống)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }
    }
}