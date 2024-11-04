package com.example.shareware;

public class Item {
    private String id;
    private String name,description,condition, category;
    private float latitude,longitude;
    private User owner,requested;
    private String primaryImage,image1,image2;
    private String status;

    public Item(){

    }

    public Item(String id, String name, String description, float latitude, float longitude, String condition, String category, User owner, String primaryImage){
        this.id = id;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.condition = condition;
        this.category = category;
        this.owner = owner;
        this.primaryImage = primaryImage;
        this.requested = null;
        this.status = "available";
    }
    //overloading constructor
    public Item(String id, String name, String description, float latitude, float longitude, String condition, String category, User owner, String primaryImage, String image1, String image2){
        this.id = id;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.condition = condition;
        this.category = category;
        this.owner = owner;
        this.primaryImage = primaryImage;
        this.image1 = image1;
        this.image2 = image2;
        this.status = "available";
        this.requested = null;
    }

    //overloading constructor
    public Item(String id, String name, String description, float latitude, float longitude, String condition, String category, User owner, String primaryImage, String image1, String image2, String status){
        this.id = id;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.condition = condition;
        this.category = category;
        this.owner = owner;
        this.primaryImage = primaryImage;
        this.image1 = image1;
        this.image2 = image2;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User getRequested() {
        return requested;
    }

    public void setRequested(User requested) {
        this.requested = requested;
    }

    public String getPrimaryImage() {
        return primaryImage;
    }

    public void setPrimaryImage(String primaryImage) {
        this.primaryImage = primaryImage;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
