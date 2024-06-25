package com.example.incercarelicenta.clase;

public class Comment {
    private String id;
    private String userId;
    private String username;
    private String text;
    private int parfumId;

    public Comment() {}

    public Comment(String id, String userId, String username, String text, int parfumId) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.text = text;
        this.parfumId=parfumId;
    }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public int getParfumId() {
        return parfumId;
    }
    public void setParfumId(int parfumId) {
        this.parfumId = parfumId;
    }
}

