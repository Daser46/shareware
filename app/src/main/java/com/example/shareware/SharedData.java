package com.example.shareware;

public class SharedData {
    private static User currentUser;
    private static Item onViewItem;

    public SharedData(){
        //empty constructor
    }

    public void setUser(User user){
        this.currentUser = user;
    }

    public User getCurrentUser(){
        return this.currentUser;
    }

    public static Item getOnViewItem() {
        return onViewItem;
    }

    public static void setOnViewItem(Item onViewItem) {
        SharedData.onViewItem = onViewItem;
    }
}
