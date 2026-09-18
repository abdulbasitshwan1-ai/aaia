package com.example.game.engine

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

class AudioSynthesizer {

    private var audioTrack: AudioTrack? = null
    private var synthesisJob: Job? = null
    private val sampleRate = 22050
    @Volatile private var isPlaying = false
    @Volatile private var targetFrequency = 70.0 // Hz (idle engine hum)
    @Volatile private var targetVolume = 0.05f

    fun start(scope: CoroutineScope) {
        if (isPlaying) return
        isPlaying = true
        synthesisJob = scope.launch(Dispatchers.Default) {
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(sampleRate / 10)

            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .build()
                )
                .setBufferSizeInBytes(minBufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            try {
                audioTrack?.play()
                val buffer = ShortArray(minBufferSize / 2)
                var phase = 0.0

                while (isActive && isPlaying) {
                    val freq = targetFrequency
                    val vol = targetVolume
                    val phaseIncrement = 2.0 * Math.PI * freq / sampleRate

                    for (i in buffer.indices) {
                        // Blend fundamental frequency with 2nd harmonic for rich engine rumble
                        val sample = (sin(phase) * 0.7 + sin(phase * 2.0) * 0.3) * vol * Short.MAX_VALUE
                        buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        phase += phaseIncrement
                        if (phase > 2.0 * Math.PI) {
                            phase -= 2.0 * Math.PI
                        }
                    }
                    audioTrack?.write(buffer, 0, buffer.size)
                }
            } catch (e: Exception) {
                // Audio synthesis gracefully handles interruptions
            } finally {
                try {
                    audioTrack?.stop()
                    audioTrack?.release()
                } catch (e: Exception) {}
                audioTrack = null
            }
        }
    }

    fun updateEngineSound(rpm: Int, throttle: Float, isNitro: Boolean) {
        // Map 800-8000 RPM to 55Hz - 380Hz
        val baseFreq = 55.0 + (rpm - 800).coerceAtLeast(0) * (330.0 / 7200.0)
        targetFrequency = if (isNitro) baseFreq * 1.25 else baseFreq
        targetVolume = (0.04f + throttle * 0.08f + if (isNitro) 0.04f else 0.0f).coerceIn(0.02f, 0.15f)
    }

    fun stop() {
        isPlaying = false
        synthesisJob?.cancel()
        synthesisJob = null
    }
}
