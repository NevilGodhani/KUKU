package com.example.kuku;

public class Accessory {

    private String title;
    private String imageUrl;
    private Double price;

    public Accessory() {
        // Default constructor required for calls to DataSnapshot.getValue(Product.class)
    }

    public Accessory(String title, String imageUrl, Double price) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.price = price;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}

