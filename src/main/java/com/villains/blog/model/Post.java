package com.villains.blog.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
public class Post {

    @Id
    private String postId;

    @TextIndexed
    private String body;
    @TextIndexed(weight = 2)
    private String userId;

    private List<Rating> rating = new ArrayList<>();


    private Long postTimeStamp;

    public List<Rating> getRating() {
        return rating;
    }

    public void setRating(String user, Boolean like) {
        Rating rating = new Rating(postId);
        rating.setLikeIt(like);
        rating.setUserId(user);
        this.rating.add(rating);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getPostTimeStamp() {
        return postTimeStamp;
    }

    public void setPostTimeStamp(Long postTimeStamp) {
        this.postTimeStamp = postTimeStamp;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }


    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


    @Override
    public String toString(){
        return "Villains Post : id = " + postId + " , rating = " + rating +
                " , timestamp = " + postTimeStamp + " ,  body =  " + body + "}";
    }

}
