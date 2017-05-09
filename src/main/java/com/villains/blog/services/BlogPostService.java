package com.villains.blog.services;

import com.villains.blog.model.Post;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by : angelo
 * Date: 01/05/2017
 */
public interface BlogPostService<T> {

    public List<T> getPosts();

    public Collection<T> getPostsByUser(String userId);

    public Collection<T> getPostsByTimeStamp(Long timeStamp);

    public T createPost(@NotNull T body);

    public void deletePost(@NotNull String postId);

    public T updatePost(@NotNull T post);

    public Post getPost(@NotNull  String postId);

    public Post addPostRating(@NotNull String postId, @NotNull Boolean rate, Optional<String> userId);

    public List<T> fullTextPostSearch(String criteria);
    
}
