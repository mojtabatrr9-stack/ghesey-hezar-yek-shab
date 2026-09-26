package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

object SilkRoadSoundEngine {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    var isSoundEnabled: Boolean = true

    fun playCamelBell() {
        if (!isSoundEnabled) return
        scope.launch {
            playSynthesized(durationMs = 260) { t, progress ->
                val env = exp(-progress * 6.5)
                val f1 = sin(2.0 * PI * 880.0 * t)
                val f2 = 0.5 * sin(2.0 * PI * 1320.0 * t)
                val f3 = 0.25 * sin(2.0 * PI * 1760.0 * t)
                ((f1 + f2 + f3) * env * 0.35).toFloat()
            }
        }
    }

    fun playWarDrum() {
        if (!isSoundEnabled) return
        scope.launch {
            playSynthesized(durationMs = 280) { t, progress ->
                val env = exp(-progress * 7.5)
                val freq = 135.0 - progress * 55.0
                val drum = sin(2.0 * PI * freq * t)
                val jingle = (Random.nextFloat() * 2f - 1f) * 0.18f * exp(-progress * 14.0).toFloat()
                (drum * env * 0.55 + jingle).toFloat()
            }
        }
    }

    fun playTowerRelocate() {
        if (!isSoundEnabled) return
        scope.launch {
            playSynthesized(durationMs = 300) { t, progress ->
                val env = sin(PI * progress)
                val sweepFreq = 260.0 + progress * 380.0
                val tone = sin(2.0 * PI * sweepFreq * t)
                val wind = (Random.nextFloat() * 2f - 1f) * 0.25f
                ((tone * 0.45 + wind * 0.35) * env * 0.45).toFloat()
            }
        }
    }

    fun playArrowShot() {
        if (!isSoundEnabled) return
        scope.launch {
            playSynthesized(durationMs = 95) { t, progress ->
                val env = exp(-progress * 10.0)
                val freq = 520.0 - progress * 240.0
                val twang = sin(2.0 * PI * freq * t)
                val air = (Random.nextFloat() * 2f - 1f) * 0.3f
                ((twang * 0.5 + air * 0.5) * env * 0.28).toFloat()
            }
        }
    }

    fun playCatapultBoom() {
        if (!isSoundEnabled) return
        scope.launch {
            playSynthesized(durationMs = 220) { t, progress ->
                val env = exp(-progress * 6.0)
                val rumble = sin(2.0 * PI * (95.0 - progress * 40.0) * t)
                val grit = (Random.nextFloat() * 2f - 1f) * 0.35f
                ((rumble * 0.65 + grit * 0.35) * env * 0.45).toFloat()
            }
        }
    }

    fun playEagleCry() {
        if (!isSoundEnabled) return
        scope.launch {
            playSynthesized(durationMs = 340) { t, progress ->
                val env = sin(PI * progress) * exp(-progress * 2.2)
                val freq = 1650.0 - progress * 420.0 + sin(progress * 28.0) * 60.0
                (sin(2.0 * PI * freq * t) * env * 0.32).toFloat()
            }
        }
    }

    fun playGoldChime() {
        if (!isSoundEnabled) return
        scope.launch {
            playSynthesized(durationMs = 210) { t, progress ->
                val env = exp(-progress * 5.5)
                val freq = if (progress < 0.45) 1046.5 else 1318.5
                (sin(2.0 * PI * freq * t) * env * 0.35).toFloat()
            }
        }
    }

    private fun playSynthesized(durationMs: Int, generator: (tSec: Double, progress: Double) -> Float) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * durationMs) / 1000
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val progress = i.toDouble() / numSamples
                val sample = generator(t, progress).coerceIn(-1f, 1f)
                buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
            }

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            Thread.sleep(durationMs.toLong() + 30L)
            track.stop()
            track.release()
        } catch (_: Throwable) {
            // Ignore audio hardware issues on headless test environments
        }
    }
}
