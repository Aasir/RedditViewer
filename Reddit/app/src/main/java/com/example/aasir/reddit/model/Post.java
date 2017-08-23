package com.example.aasir.reddit.model;

import java.io.Serializable;

/**
 * Created by Aasir on 7/9/2017.
 */

public class Post implements Serializable {

    private String title;
    private String author;
    private String date_updated;
    private String postURl;
    private String thumbnailURL;
    private String id;

    public Post(String title, String author, String date_updated, String postURl, String thumbnailURL, String id) {
        this.title = title;
        this.author = author;
        this.date_updated = date_updated;
        this.postURl = postURl;
        this.thumbnailURL = thumbnailURL;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate_updated() {
        return date_updated;
    }

    public void setDate_updated(String date_updated) {
        this.date_updated = date_updated;
    }

    public String getPostURl() {
        return postURl;
    }

    public void setPostURl(String postURl) {
        this.postURl = postURl;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    @Override
    public String toString() {
        return "Post{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", date_updated='" + date_updated + '\'' +
                ", postURl='" + postURl + '\'' +
                ", thumbnailURL='" + thumbnailURL + '\'' +
                '}';
    }
}
