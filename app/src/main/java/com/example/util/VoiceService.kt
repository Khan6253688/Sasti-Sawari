package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

object VoiceService {
  private const val TAG = "VoiceService"
  private var tts: TextToSpeech? = null
  private var isInitialized = false

  fun init(context: Context) {
    if (tts != null) return
    try {
      tts = TextToSpeech(context.applicationContext) { status ->
        if (status == TextToSpeech.SUCCESS) {
          isInitialized = true
          // Try setting Urdu or Pakistani English locale
          val urduLocale = Locale("ur", "PK")
          val result = tts?.setLanguage(urduLocale)
          if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts?.setLanguage(Locale.ENGLISH)
          }
          tts?.setSpeechRate(0.95f)
        } else {
          Log.e(TAG, "TTS Initialization failed with status: $status")
        }
      }
    } catch (e: Exception) {
      Log.e(TAG, "Failed to create TextToSpeech", e)
    }
  }

  fun playRideStartVoice(destination: String = "", stand: String = "") {
    if (!isInitialized || tts == null) return
    try {
      val text = if (destination.isNotBlank()) {
        "Safar shuru ho gaya hai. Sasti Sawari me khushamdeed! Destination $destination. Allah hifazat farmaye."
      } else {
        "Safar shuru ho gaya hai. Sasti Sawari me khushamdeed! Allah hifazat farmaye."
      }
      tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "RIDE_START_VOICE")
    } catch (e: Exception) {
      Log.e(TAG, "Error playing ride start voice", e)
    }
  }

  fun playRideCompleteVoice(fareAmount: Int) {
    if (!isInitialized || tts == null) return
    try {
      val text = "Safar mukammal ho gaya hai. Sasti Sawari istemal karne ka shukriya! Total kiraya $fareAmount rupay hai."
      tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "RIDE_COMPLETE_VOICE")
    } catch (e: Exception) {
      Log.e(TAG, "Error playing ride complete voice", e)
    }
  }

  fun shutdown() {
    try {
      tts?.stop()
      tts?.shutdown()
      tts = null
      isInitialized = false
    } catch (e: Exception) {
      Log.e(TAG, "Error shutting down TTS", e)
    }
  }
}
