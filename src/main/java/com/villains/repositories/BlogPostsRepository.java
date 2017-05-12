package com.villains.repositories;

import com.villains.blog.model.Post;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource ( collectionResourceRel = "blog-villains", path = "blog-villains")
public interface BlogPostsRepository extends MongoRepository<Post,String> {

    List<Post> findAllByUserId(@Param("userId") String userId);
    List<Post> findAllBy(TextCriteria criteria);

}