package com.villains.blog.services;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.villains.blog.model.Post;
import com.villains.repositories.BlogPostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BlogPostServiceImpl implements BlogPostService<Post> {

    @Autowired
    private BlogPostsRepository reporistory;

    @Autowired
    private MongoTemplate mongoTemplate;


    private  final TextIndexDefinition textIndex = new TextIndexDefinition.TextIndexDefinitionBuilder()
            .onField("body", 1F)
            .onField("userId", 2F)
            .build();


    @Autowired
    public BlogPostServiceImpl (BlogPostsRepository rep){
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
    public void deletePost(String postId) throws Exception {

        if (reporistory.findOne(postId) == null){
               throw new Exception("Post id not found");
        }
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

        mongoTemplate.indexOps(Post.class).ensureIndex(textIndex);
        TextCriteria criteria = TextCriteria.forDefaultLanguage()
                .matchingAny(textToSearch);

        Query query = TextQuery.queryText(criteria)
                .sortByScore()
                .with(new PageRequest(0, 5));

        return mongoTemplate.find(query, Post.class);

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
