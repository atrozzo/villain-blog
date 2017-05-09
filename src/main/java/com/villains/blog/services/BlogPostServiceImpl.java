package com.villains.blog.services;

import com.villains.blog.model.Post;
import com.villains.repositories.BlogPostsReporistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BlogPostServiceImpl implements BlogPostService<Post> {

    @Autowired
    private BlogPostsReporistory reporistory;

    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    public BlogPostServiceImpl ( BlogPostsReporistory rep){
        reporistory = rep;
    }

    @Override
    public List<Post> getPosts() {
        return reporistory.findAll();
    }


    @Override
    public List<Post> getPostsByUser(String userId) {
        Example<String> search = Example.of(userId);
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        List<Post> result = mongoTemplate.find(query, Post.class);

        return result;
    }

    @Override
    public List<Post> getPostsByTimeStamp(Long timeStamp) {
        return null;
    }

    @Override
    public Post createPost( Post newPost) {
        newPost.setPostTimeStamp(Instant.now().toEpochMilli() );

        return reporistory.save(newPost);

    }

    @Override
    public void deletePost(String postId) {
        reporistory.delete(postId);
    }

    @Override
    public Post updatePost(Post post) {
        Post postToUpdate = reporistory.findOne(post.getPostId());

        if(postToUpdate != null)  {
            postToUpdate.setBody(post.getBody());
            return reporistory.save(postToUpdate);
        }
        return postToUpdate;
    }

    @Override
    public Post getPost(String postId) {
        return reporistory.findOne(postId);
    }

    @Override
    public Post addPostRating(String postId, Boolean like, Optional<String> userId) {
        Post post = reporistory.findOne(postId);
        if (post != null){
            post.setRating(userId.orElse("unavailable"), like);
            return reporistory.save(post);
        }
        return post;

    }

    @Override
    public List<Post> fullTextPostSearch(String textToSearch) {
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(textToSearch);

        return reporistory.findAllBy(criteria);
    }


    public List<Post> searchPostsByUserAndTime(String user, String time) throws Exception {

        Query query = new Query();
        if (time !=null && user == null ) {
            query.addCriteria(Criteria.where("postTimeStamp").gt(getDateFromString(time).getTime()));
        }else if (user != null && time == null){
            query.addCriteria(Criteria.where("userId").regex(user, "i")) ;
        } else {
            query.addCriteria(Criteria.where("postTimeStamp").gt(getDateFromString(time).getTime()).and("userId").regex(user,"i"));

        }

        return mongoTemplate.find(query,Post.class);

    }

    public Collection searchText(String text) {
        return mongoTemplate.find(Query.query(new Criteria()
                .orOperator(Criteria.where("body").regex(text, "i"))
        ), Post.class);
    }



    private Date getDateFromString(String dateToParse) throws  ParseException{
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSX");
        return formatter.parse(dateToParse);

    }





}
