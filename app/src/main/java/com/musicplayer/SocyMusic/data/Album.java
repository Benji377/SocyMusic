package com.musicplayer.SocyMusic.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Album {
    @PrimaryKey
    @ColumnInfo(name = "album_id")
    @NonNull
    private UUID id;
    @ColumnInfo(name = "album_title")
    private String title;
    @ColumnInfo(name = "album_art_path")
    private String artPath;
    @Ignore
    private List<Song> songList;

    public Album(@NonNull UUID id, String title, String artPath) {
        this.id = id;
        this.title = title;
        this.artPath = artPath;
        songList = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSongCount() {
        return songList == null ? 0 : songList.size();
    }

    public void addSong(Song song) {
        songList.add(song);
    }

    public String getArtPath() {
        return artPath;
    }

    public void setArtPath(String artPath) {
        this.artPath = artPath;
    }

    @NonNull
    public UUID getId() {
        return id;
    }

    public void setId(@NonNull UUID id) {
        this.id = id;
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }

    public boolean containsSong(Song song) {
        return songList.contains(song);
    }

    public boolean isEmpty() {
        return songList.isEmpty();
    }

    public List<Song> getSongList() {
        return songList;
    }
}
