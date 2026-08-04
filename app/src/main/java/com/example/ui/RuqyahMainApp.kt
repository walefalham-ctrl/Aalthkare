package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.RuqyahBottomNav
import com.example.ui.components.RuqyahTopBar
import com.example.ui.components.SafeContentBoundary
import com.example.ui.components.ToastNotification
import com.example.ui.screens.AzkarDeckScreen
import com.example.ui.screens.GodNamesScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.QuranRuqyahScreen
import com.example.ui.theme.MyApplicationTheme

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RuqyahMainApp(
    viewModel: RuqyahViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    MyApplicationTheme(darkTheme = uiState.isDarkMode) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Scaffold(
                topBar = {
                    RuqyahTopBar(
                        formattedDateArabic = uiState.formattedDateArabic,
                        onStartNewDay = { viewModel.startNewDayConfirm() },
                        isDarkMode = uiState.isDarkMode,
                        onToggleDarkMode = { viewModel.toggleDarkMode(it) }
                    )
                },
                bottomBar = {
                    RuqyahBottomNav(
                        activeTab = uiState.activeTab,
                        onTabSelected = { viewModel.selectTab(it) }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AnimatedContent(
                        targetState = uiState.activeTab,
                        transitionSpec = { fadeIn() with fadeOut() },
                        label = "tabContent"
                    ) { tab ->
                        SafeContentBoundary(
                            screenName = tab.title
                        ) {
                            when (tab) {
                                AppTab.HOME -> HomeScreen(
                                    uiState = uiState,
                                    onToggleTask = { viewModel.toggleTask(it) },
                                    onUpdateEffectNote = { viewModel.updateEffectNote(it) },
                                    onSaveRecord = { viewModel.saveTodayRecordAndNote() },
                                    onUpdateObservationNote = { viewModel.updateObservationNoteInput(it) },
                                    onSelectObservationMood = { viewModel.selectObservationMood(it) },
                                    onAddObservationLog = { viewModel.addObservationLog() },
                                    onDeleteObservationLog = { viewModel.deleteObservationLog(it) },
                                    onShowToast = { viewModel.showToast(it) },
                                    onToggleFamilyDuaa = { viewModel.toggleFamilyDuaa(it) },
                                    onToggleDarkMode = { viewModel.toggleDarkMode(it) },
                                    onExportLogs = { viewModel.exportObservationLogsToFile() },
                                    onDismissExportDialog = { viewModel.dismissExportDialog() },
                                    onImportLogsFromJson = { viewModel.importObservationLogsFromJson(it) }
                                )

                                AppTab.AZKAR -> AzkarDeckScreen(
                                    uiState = uiState,
                                    onSelectCategory = { viewModel.selectZikrCategory(it) },
                                    onNextZikr = { viewModel.nextZikr() },
                                    onPrevZikr = { viewModel.prevZikr() },
                                    onTapCounter = { viewModel.incrementCurrentZikr() },
                                    onResetCounter = { viewModel.resetCurrentZikr() },
                                    onTapZikrItem = { viewModel.incrementZikrItem(it) },
                                    onResetZikrItem = { viewModel.resetZikrItem(it) }
                                )

                                AppTab.QURAN -> QuranRuqyahScreen(
                                    uiState = uiState,
                                    onNextVerse = { viewModel.nextRuqyahVerse() },
                                    onPrevVerse = { viewModel.prevRuqyahVerse() },
                                    onCompleteVerseAndNext = { viewModel.completeCurrentVerseAndAdvance() },
                                    onSelectVerseIndex = { viewModel.selectRuqyahVerseIndex(it) },
                                    onResetVerseProgress = { viewModel.resetRuqyahVerseProgress() },
                                    onUpdateObservationNote = { viewModel.updateObservationNoteInput(it) },
                                    onSelectObservationMood = { viewModel.selectObservationMood(it) },
                                    onAddObservationLog = { viewModel.addObservationLog() },
                                    onDeleteObservationLog = { viewModel.deleteObservationLog(it) }
                                )

                                AppTab.NAMES -> GodNamesScreen(
                                    uiState = uiState,
                                    onSelectGodName = { viewModel.selectGodName(it) }
                                )

                                AppTab.HISTORY -> HistoryScreen(
                                    uiState = uiState,
                                    onClearHistory = { viewModel.clearAllHistory() },
                                    onDeleteObservationLog = { viewModel.deleteObservationLog(it) },
                                    onExportLogs = { viewModel.exportObservationLogsToFile() },
                                    onDismissExportDialog = { viewModel.dismissExportDialog() },
                                    onImportLogsFromJson = { viewModel.importObservationLogsFromJson(it) },
                                    onShowToast = { viewModel.showToast(it) }
                                )
                            }
                        }
                    }

                // Toast Notification Overlay
                Box(
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    ToastNotification(
                        message = uiState.toastMessage,
                        onDismiss = { viewModel.clearToast() }
                    )
                }
            }
        }
    }
}
}
