package com.lucasj.gamedev.essentials.audio;

import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.sound.sampled.*;

import com.lucasj.gamedev.Assets.SpriteTools;
import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.misc.Debug;

public class AudioPlayer {

    public static final String SFX_DIRECTORY = SpriteTools.assetDirectory + "SFX/";

    private final List<AudioClip> audioClips = new ArrayList<>();
    private final Map<String, Clip> clipCache = new HashMap<>();
    private final Map<String, Integer> playingCounts = new HashMap<>();

    private final Map<String, byte[]> audioDataCache = new HashMap<>();
    private final Map<String, AudioFormat> audioFormatCache = new HashMap<>();
    private final Game game;

    public AudioPlayer(Game game) {
        this.game = game;
    }

    public void update() {
        Iterator<AudioClip> iterator = audioClips.iterator();
        while (iterator.hasNext()) {
            AudioClip audioClip = iterator.next();
            audioClip.update();
            if (audioClip.hasFinishedPlaying()) {
                audioClip.cleanUp();
                decrementPlayingCount(audioClip.getFileName());
                iterator.remove();
            }
        }
    }

    public void playMusic(String fileName, float volumeMultiplier) {
        playClip(fileName, null, volumeMultiplier, true);
    }

    public void playSound(String fileName, Vector2D position, float volumeMultiplier) {
        playClip(fileName, position, volumeMultiplier, false);
    }

    public void playSound(String fileName, Vector2D position) {
        playClip(fileName, position, 1, false);
    }

    private void playClip(String fileName, Vector2D position, float volumeMultiplier, boolean isMusic) {
        String fullPath = SFX_DIRECTORY + fileName;

        if (playingCounts.getOrDefault(fileName, 0) >= 3) {
            return; // Limit the number of simultaneous plays per file
        }

        byte[] audioData = audioDataCache.get(fullPath);
        AudioFormat format = audioFormatCache.get(fullPath);

        if (audioData == null || format == null) {
            // Load and cache the audio data
            try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(fullPath))) {
                format = audioInputStream.getFormat();
                audioData = audioInputStream.readAllBytes();
                audioDataCache.put(fullPath, audioData);
                audioFormatCache.put(fullPath, format);
            } catch (IOException | UnsupportedAudioFileException e) {
                logError("Failed to load sound: " + fullPath);
                return;
            }
        }

        try {
            Clip clip = AudioSystem.getClip();
            clip.open(format, audioData, 0, audioData.length);

            AudioClip audioClip = isMusic
                    ? new MusicClip(clip, game, volumeMultiplier, fileName)
                    : new SoundClip(clip, game, position, volumeMultiplier, fileName);

            audioClips.add(audioClip);
            incrementPlayingCount(fileName);
            clip.start();
        } catch (LineUnavailableException e) {
            logError("Unable to play sound: " + fileName);
        }
    }



    private Clip loadClip(String filePath) {
        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath))) {
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.setMicrosecondPosition(0);
            return clip;
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            logError(e.getMessage());
        }
        return null;
    }

    private void incrementPlayingCount(String fileName) {
        playingCounts.put(fileName, playingCounts.getOrDefault(fileName, 0) + 1);
    }

    private void decrementPlayingCount(String fileName) {
        playingCounts.put(fileName, Math.max(0, playingCounts.getOrDefault(fileName, 0) - 1));
    }

    private void logError(String message) {
        Debug.log(this, message);
    }
}
