package com.aghakhani.awareness;

public class Audio {
    private int id;
    private String title;
    private String audioUrl;
    private String thumbnailUrl;

    // Constructor
    public Audio(int id, String title, String audioUrl, String thumbnailUrl) {
        this.id = id;
        this.title = title;
        this.audioUrl = audioUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
}