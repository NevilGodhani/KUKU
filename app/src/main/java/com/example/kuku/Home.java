package com.example.kuku;

public class Home {
    private String title;
    private String imageUrl;

    public Home() {
        // Default constructor required for calls to DataSnapshot.getValue(Upload.class)
    }

    public Home(String title, String imageUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
