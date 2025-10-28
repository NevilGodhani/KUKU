package com.example.kuku;

public class ImageItem {
    private String title;
    private String imageUrl;

    public ImageItem() {
        // Default constructor required for calls to DataSnapshot.getValue(ImageItem.class)
    }

    public ImageItem(String title, String imageUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
