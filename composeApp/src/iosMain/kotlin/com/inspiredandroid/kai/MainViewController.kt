package com.inspiredandroid.kai

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import androidx.navigation.compose.rememberNavController
import com.posthog.kmp.PostHog
import com.posthog.kmp.PostHogConfig
import com.posthog.kmp.PostHogContext
import nl.marc_apps.tts.TextToSpeechEngine
import nl.marc_apps.tts.TextToSpeechInstance
import nl.marc_apps.tts.rememberTextToSpeechOrNull

fun MainViewController() = ComposeUIViewController {
    LaunchedEffect(Unit) {
        PostHog.setup(
            config = PostHogConfig(
                apiKey = PostHogBuildConfig.API_KEY,
                host = PostHogBuildConfig.HOST,
            ),
            context = PostHogContext(),
        )
    }
    // Defer TTS initialization until after the first frame
    var ttsReady by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { ttsReady = true }
    val textToSpeech: TextToSpeechInstance? = if (ttsReady) {
        rememberTextToSpeechOrNull(TextToSpeechEngine.SystemDefault)
    } else {
        null
    }

    val navController = rememberNavController()
    App(
        navController = navController,
        textToSpeech = textToSpeech,
    )
}
