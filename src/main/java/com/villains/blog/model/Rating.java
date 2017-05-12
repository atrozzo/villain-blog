package com.villains.blog.model;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by : angelo
 * Date: 09/05/2017
 */

@Document
public class Rating {

    Rating(String postId){
        this.postId = postId;
    }

    private String userId;

    private Boolean likeIt;

    private String postId;

    public Boolean getLikeIt() {
        return likeIt;
    }

    public void setLikeIt(Boolean likeIt) {
        this.likeIt = likeIt;
    }
    
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
