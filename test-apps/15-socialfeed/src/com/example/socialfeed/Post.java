package com.example.socialfeed;

public class Post {
    public User author;
    public String text;
    public String imageEmoji;  // emoji as image placeholder, null for text-only
    public String time;
    public int likes;
    public int comments;
    public boolean liked;

    public Post(User author, String text, String imageEmoji, String time, int likes, int comments, boolean liked) {
        this.author = author;
        this.text = text;
        this.imageEmoji = imageEmoji;
        this.time = time;
        this.likes = likes;
        this.comments = comments;
        this.liked = liked;
    }
}
