package com.villains.blog.controllers;

import com.villains.blog.model.Post;
import com.villains.blog.services.BlogPostServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping( value = "/villains/" )
public class BlogController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private BlogPostServiceImpl postService;


    // API GET
    @RequestMapping(method = RequestMethod.GET, value = "/posts")
    @ResponseBody
    public List<Post> findAllPosts() {
        return postService.getPosts();
    }
    // find one
    @RequestMapping(method = RequestMethod.GET, value = "/posts/{postId}")
    @ResponseBody
    public ResponseEntity<Post> findPost(@PathVariable String postId) {
        Post post = postService.getPost(postId) ;
        if (post == null)
            return new ResponseEntity<Post>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<Post>(post, HttpStatus.FOUND);

    }
    // find by criteria : user and/or timestamp.

    @RequestMapping(method = RequestMethod.GET, value = "/posts/filter")
    @ResponseBody
    public List<Post> findPostsByCriteria(@RequestParam(value = "userId", required = false) String userId,
                                          @RequestParam( value = "from" , required = false)
                                          @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSSSSSX")  String time,
                                          @RequestParam(value = "order", required = false) String ordering) throws Exception {



        return postService.searchPostsByUserAndTime(userId, time );
    }

    // Get all post from a single user .
    @RequestMapping(method = RequestMethod.GET, value = "posts/user/{userId}")
    @ResponseBody
    public List<Post> findAllUserPosts(@PathVariable String  userId)  {
        return postService.getPostsByUser(userId);
    }
    // API CREATE

    // create a single post.
    @RequestMapping(method=RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity create(@RequestBody Post postBody) {
        Assert.notNull(postBody, "Post message cannot be null");
        LOGGER.info("Creating a new Post ...");

        Post result = postService.createPost(postBody) ;
        if (result ==null)
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<Post>(result , HttpStatus.CREATED);

    }
    // delete a single post
    @RequestMapping(method=RequestMethod.DELETE, value="/posts/{id}")
    public void delete(@PathVariable String id) throws Exception {
        postService.deletePost(id);

    }
    // update a single post.
    @RequestMapping(method=RequestMethod.PUT, value="/posts/{postId}")
    public Post update( @PathVariable String postId,
                        @RequestParam(value = "body", required = true ) String body ) {
        Post toUpdate = new Post();
        toUpdate.setBody(body);
        toUpdate.setPostId(postId);
        return postService.updatePost(toUpdate);
    }

    // Add a rate to an existing post
    @RequestMapping(method=RequestMethod.PUT, value="/rating/{postId}")
    @ResponseBody
    Post addRating( @PathVariable String postId,
                    @RequestParam(value = "userId", required = false ) String userId,
                    @RequestParam( value = "rate" , required = true) Boolean rate) throws Exception {

        return postService.addPostRating(postId, rate, Optional.ofNullable(userId));
    }


    // Full text functionality.
    @RequestMapping(method=RequestMethod.GET, value="/search")
    public List<Post> fullTextSearch(@RequestParam String search) {
        return postService.fullTextPostSearch(search);
    }

}
