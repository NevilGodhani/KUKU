package com.example.kuku;


public class Cart {

    private String title;
    private double price;
    private String imageUrl;
    private Integer qty;

    public  Cart () {
        // Default constructor required for calls to DataSnapshot.getValue(Product.class)
    }

    public  Cart (String title, double price, String imageUrl,Integer qty) {
        this.title = title;
        this.price = price;
        this.imageUrl = imageUrl;
        this.qty=qty;
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

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
}

