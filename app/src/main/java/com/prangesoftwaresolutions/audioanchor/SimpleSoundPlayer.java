package com.prangesoftwaresolutions.audioanchor;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;


/**
 * Class to simplify working with sounds.
 */

class SimpleSoundPlayer {

    private static final String LOG_TAG = SimpleSoundPlayer.class.getName();

    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;

    /*
     * Listener that gets triggered when the audio focus changes
     */
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
            // AUDIOFOCUS_LOSS_TRANSIENT case means that we've lost audio focus for a short amount of time.
            mMediaPlayer.pause();
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            // we have regained focus and can resume playback.
            mMediaPlayer.start();
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            // we've lost audio focus -> stop playback and clean up resources
            releaseMediaPlayer();
        }
        }
    };

    SimpleSoundPlayer(AudioManager audioManager) {
        mAudioManager = audioManager;
    }

    void initialize(String path, int position) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
            mMediaPlayer.seekTo(position);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem playing audio file: ", e);
        }
    }

    /*
     * Play the audio file stored at the specified path
     */
    boolean play() {
        // Request audio focus for playback
        int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED  && getCurrentPosition() != getDuration()) {
            mMediaPlayer.start();
            return true;
        }
        return false;
    }

    /*
     * Pause playback
     */
    void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    /*
     * Skip the specified amount of seconds forward
     */
    void forward(int seconds) {
        mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() + seconds*1000);
    }

    /*
     * Skip the specified amount of seconds backward
     */
    void backward(int seconds) {
        mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() - seconds*1000);
    }

    /*
     * Get total duration of the audio file
     */
    int getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    /*
     * Get current position of the played audio file
     */
    int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /*
     * Set current position of the played audio file
     */
    void setCurrentPosition(int progress) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(progress);
        }
    }

    /*
     * Set a completion listener for the media player
     */
    void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        mMediaPlayer.setOnCompletionListener(listener);
    }

    /*
     * Releases the memory currently used by the MediaPlayer if the Player exists
     */
    void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }

    /*
     * Returns true if the MediaPlayer is currently playing, false otherwise
     */
    boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }
}
