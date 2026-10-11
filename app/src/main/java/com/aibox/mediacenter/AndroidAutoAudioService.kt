package com.aibox.mediacenter

import android.os.Bundle
import androidx.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat

/**
 * Estrutura inicial para navegação de mídia compatível com clientes MediaBrowser.
 *
 * Este serviço ainda não reproduz áudio nem anuncia faixas. Para uso real,
 * implemente uma fonte de conteúdo autorizada e conecte um player ao MediaSession.
 * Isso não habilita aplicativos de vídeo no Android Auto projetado.
 */
class AndroidAutoAudioService : MediaBrowserServiceCompat() {
    private lateinit var mediaSession: MediaSessionCompat

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSessionCompat(this, "AIBoxMediaCenter").apply {
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    // TODO: iniciar o player real depois de integrar uma fonte de áudio.
                }

                override fun onPause() {
                    // TODO: pausar o player real.
                }

                override fun onStop() {
                    // TODO: parar o player real.
                }
            })
            setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setActions(
                        PlaybackStateCompat.ACTION_PLAY or
                            PlaybackStateCompat.ACTION_PAUSE or
                            PlaybackStateCompat.ACTION_STOP
                    )
                    .setState(PlaybackStateCompat.STATE_NONE, 0L, 1f)
                    .build()
            )
            isActive = true
        }
        sessionToken = mediaSession.sessionToken
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot = BrowserRoot("AI_BOX_ROOT", null)

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaItem>>
    ) {
        result.sendResult(mutableListOf())
    }

    override fun onDestroy() {
        if (::mediaSession.isInitialized) {
            mediaSession.isActive = false
            mediaSession.release()
        }
        super.onDestroy()
    }
}
