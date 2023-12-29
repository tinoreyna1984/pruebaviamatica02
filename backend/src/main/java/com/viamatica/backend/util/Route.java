package com.viamatica.backend.util;

public enum Route {
    HOME("/"),
    USERS("/users"),
    ABOUT("/about");

    private String path;

    Route(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
