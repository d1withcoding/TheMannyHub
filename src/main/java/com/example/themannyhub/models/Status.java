package com.example.themannyhub.models;

public enum Status {

    ACTIVE("Active"),

    INACTIVE("Inactive");
    // This is the status indicator
    private final String displayName;

    Status(String displayName){
        this.displayName = displayName;
    }

    // method to return display name as String
    public  String getDisplayName(){
        return displayName;
    }

}
