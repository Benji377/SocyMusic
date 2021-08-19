package com.musicplayer.SocyMusic;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class SongsData {
    public static SongsData data;
    private ArrayList<Song> allSongs;
    private ArrayList<Song> playingQueue;
    private ArrayList<Song> originalQueue;
    private int playingQueueIndex;
    private boolean repeat;
    private boolean shuffle;

    /**
     * SongsData custom constructor.
     * When created automatically reloads all songs and creates the playingQueue
     */
    private SongsData(@NonNull Context context) {
        reloadSongs(context);
        playingQueue = new ArrayList<>();
    }

    /**
     * Method to retrieve the song that is being played
     *
     * @return Song at which the playingQueueIndex points at
     */
    public Song getSongPlaying() {
        return playingQueue.get(playingQueueIndex);
    }

    /**
     * Plays the next song by grabbing the next song in the playingQueueIndex
     * WARNING: Might cause ArrayOutOfBounds Exception!
     *
     * @return the next Song that will be played
     */
    public Song playNext() {
        setPlayingIndex(playingQueueIndex + 1);
        return getSongPlaying();
    }

    /**
     * Plays the previous song by grabbing the previous song in the playingQueueIndex
     * WARNING: Might cause ArrayOutOfBounds Exception if Index is 0
     *
     * @return the previous song that will be played
     */
    public Song playPrev() {
        setPlayingIndex(playingQueueIndex - 1);
        return getSongPlaying();
    }

    /**
     * Sets the playingQueueIndex to a specific index
     *
     * @param playingIndex the index of the song
     */
    public void setPlayingIndex(int playingIndex) {
        playingQueueIndex = playingIndex;
        if (playingQueueIndex < 0 || playingQueueIndex > playingQueue.size() - 1 && repeat)
            playingQueueIndex = 0;
    }

    /**
     * Clears the previous Queue and creates a new one with all songs and starts playing from
     * a specific index/position
     *
     * @param position the index from where the player should start playing
     */
    public void playAllFrom(int position) {
        playingQueue = new ArrayList<>();
        playingQueue.addAll(allSongs);

        playingQueueIndex = position;
        originalQueue = playingQueue;
    }

    /**
     * Adds a song at the end of the Queue
     *
     * @param song the song to be added
     */
    public void addToQueue(Song song) {
        playingQueue.add(song);
    }

    /**
     * Adds a song to a specific position/index to the queue
     *
     * @param position index to where the song should be placed
     */
    public void addToQueue(int position) {
        playingQueue.add(allSongs.get(position));
    }

    /**
     * Creates an instance of the class if it doesn't already exist
     *
     * @return the new created class
     */
    public static SongsData getInstance(Context context) {
        if (data == null)
            data = new SongsData(context);
        return data;
    }

    /**
     * Loads all songs from the internal memory of the phone and overwrites/creates the allSongs list
     * This excludes SD-cards, USB, etc...
     */
    public void reloadSongs(@NonNull Context context) {

        HashSet<String> paths = new HashSet<>(PreferenceManager.getDefaultSharedPreferences(context).getStringSet(SocyMusicApp.PREFS_KEY_LIBRARY_PATHS, SocyMusicApp.defaultPathsSet));
        allSongs = new ArrayList<>();
        for (String path : paths) {
            File file = new File(path);
            if (!file.exists() || !file.canRead())
                continue;
            allSongs.addAll(loadSongs(file));
        }
    }


    /**
     * Searches for .mp3 and .wav files in the given directory. If it finds directories on its way
     * it automatically searches in them too recursively. The songs it founds get added to an array.
     * Hidden files will be ignored.
     *
     * @param dir Directory in which the songs should be searched
     * @return Array with songs
     */
    public ArrayList<Song> loadSongs(File dir) {
        // All files in given directory
        File[] files = dir.listFiles();
        // Array that stores all songs
        ArrayList<Song> songsFound = new ArrayList<>();
        if (files != null) {
            // All files in the directory
            for (File singlefile : files) {
                // If its a directory, the method gets called again with that directory as parameter
                if (singlefile.isDirectory() && !singlefile.isHidden()) {
                    songsFound.addAll(loadSongs(singlefile));
                } else {
                    // Convert file to song and add it to the array
                    if (singlefile.getName().endsWith(".mp3") || singlefile.getName().endsWith(".wav")) {
                        songsFound.add(new Song(singlefile));
                    }
                }
            }
        }

        return songsFound;
    }


    public int getPlayingQueueCount() {
        return playingQueue.size();
    }

    /**
     * Checks if song at a given position/index exists
     *
     * @param position The index to check for the song's existence
     * @return True if the song exists, else false
     */
    public boolean songExists(int position) {
        try {
            return allSongs.get(position).getFile().exists();
        } catch (IndexOutOfBoundsException ex) {
            return false;
        }
    }

    /**
     * Returns the song at a given position/index
     *
     * @param position The index to retrieve the song from
     * @return The song at the given index
     */
    public Song getSongAt(int position) {
        return allSongs.get(position);
    }

    public Song getSongFromQueueAt(int position) {
        return playingQueue.get(position);
    }

    /**
     * Returns the amount of songs that are in the Array of songs right now
     *
     * @return The size of the Array containing all the songs
     */
    public int songsCount() {
        return allSongs.size();
    }

    /**
     * Checks if the player is in repeat mode or not
     *
     * @return True if the song should be repeated, else false
     */
    public boolean isRepeat() {
        return repeat;
    }

    /**
     * Checks if the player is in shuffle mode or not
     *
     * @return True if in shuffle mode, else false
     */
    public boolean isShuffle() {
        return shuffle;
    }

    /**
     * Changes the repeat state of a song, sets if the song should be repeated or not
     *
     * @param repeat True if the song should be repeated, or false if not
     */
    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    /**
     * Changes the shuffle mode of the player
     *
     * @param shuffle True if shuffle mode should be activated
     */
    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
        if (shuffle) {
            // Create random queue
            setRandomQueue();
        } else {
            // resets queue to original
            Song songPlaying = getSongPlaying();
            playingQueue = originalQueue;
            //find where the song that was playing randomly was in the original queue
            for (int i = 0; i < playingQueue.size(); i++) {
                if (playingQueue.get(i).equals(songPlaying)) {
                    playingQueueIndex = i;
                    break;
                }
            }

        }
    }

    /**
     * Checks if the index is currently at the last position in the array
     *
     * @return True if it is the last song in the array, else false
     */
    public boolean lastInQueue() {
        return playingQueueIndex == playingQueue.size() - 1;
    }

    /**
     * Checks if the index is currently at the first position in the array
     *
     * @return True if it is the first song, else false
     */
    public boolean firstInQueue() {
        return playingQueueIndex == 0;
    }

    public List<Song> getPlayingQueue() {
        return playingQueue;
    }

    public void onQueueReordered(int from, int to) {
        if (playingQueueIndex == from)
            playingQueueIndex = to;
        else {
            if (from < to && playingQueueIndex > from && playingQueueIndex <= to)
                playingQueueIndex--;
            else if (from > to && playingQueueIndex < from && playingQueueIndex >= to)
                playingQueueIndex++;
        }
    }

    /**
     * Gets the index of the currently playing song
     *
     * @return The index of the currently playing song
     */
    public int getPlayingIndex() {
        return playingQueueIndex;
    }

    /**
     * Overried the current playingQueue and creates a random queue
     */
    public void setRandomQueue() {
        // Temporary Arraylist to store all the songs
        ArrayList<Song> temporary = new ArrayList<>();
        final Random r = new Random();
        // Avoids getting double random numbers
        // Example: The number 5 only gets called once
        final Set<Integer> s = new HashSet<>();
        // Adds the currently playing song, we dont want to shuffle this one too
        temporary.add(getSongPlaying());
        // Creates a random queue
        for (int i = 0; i < playingQueue.size() - 1; i++) {
            while (true) {
                int num = r.nextInt(playingQueue.size());
                // leaves out currently playing song
                if (!s.contains(num) && num != playingQueueIndex) {
                    s.add(num);
                    temporary.add(getSongFromQueueAt(num));
                    break;
                }
            }
        }
        // replaces queue
        playingQueue = temporary;
        playingQueueIndex = 0;
    }

    public List<Song> getAllSongs() {
        //return read-only list of all songs
        return allSongs;
    }
}
