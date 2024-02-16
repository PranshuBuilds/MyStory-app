package com.example.mystr;

 public class Story {
     private String storyid;
    private String title;
    private String description;
    private String type;
    private String author;
    private String imageUrl;
     private int likes;

    // Constructors, getters, and setters

     public Story() {
         // Default constructor required for Firebase
     }

     public Story(String storyid, String title, String description, String type, String author, String imageUrl, int likes) {
         this.storyid = storyid;
         this.title = title;
         this.description = description;
         this.type = type;
         this.author = author;
         this.imageUrl = imageUrl;
         this.likes = likes;
     }

     public int getLikes() {
         return likes;
     }

     public void setLikes(int likes) {
         this.likes = likes;
     }
     public void incrementLikes() {
         likes++;
     }


     public String getStoryid() {
         return storyid;
     }

     public void setStoryid(String storyid) {
         this.storyid = storyid;
     }

     public String getTitle() {
         return title;
     }

     public void setTitle(String title) {
         this.title = title;
     }

     public String getDescription() {
         return description;
     }

     public void setDescription(String description) {
         this.description = description;
     }

     public String getType() {
         return type;
     }

     public void setType(String type) {
         this.type = type;
     }

     public String getAuthor() {
         return author;
     }

     public void setAuthor(String author) {
         this.author = author;
     }

     public String getImageUrl() {
         return imageUrl;
     }

     public void setImageUrl(String imageUrl) {
         this.imageUrl = imageUrl;
     }
 }

