package com.villains;


import com.villains.blog.controllers.BlogController;
import com.villains.blog.model.Post;
import com.villains.blog.services.BlogPostServiceImpl;
import com.villains.repositories.BlogPostsRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc

public class BlogTests {

	@Autowired
	BlogPostsRepository repository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	BlogPostServiceImpl service;

	@Autowired
	BlogController controller;

	@Before
	public void testSetup() {


		// clean up the db ( you never know ) .
		repository.deleteAll();

		// Initialise teh collection.
		if (!mongoTemplate.collectionExists(Post.class)) {
			mongoTemplate.createCollection(Post.class);
		}
		// Create test posts.
		repository.save(createPosts());

	}

	@After
	public void tearDown() {
		mongoTemplate.dropCollection(Post.class);
	}


	@Test
	public void testShouldRetrieveAllAvailablePosts(){
		  assertThat(service.getPosts(), hasSize(4));
	}

	@Test
	public void testShouldRetrieveAllPostByUserName(){
		assertThat(service.getPostsByUser("newuser"), hasSize(2));
	}

	@Test
	public void testShouldUpdatePostInformation(){
		String body = "This is a new body post.";
		List<Post> posts = service.getPostsByUser("secondUser");
		Post update = posts.stream().findFirst().get();
		update.setBody(body);
		assertThat(service.updatePost(update).getBody(),containsString(body) );
	}

	 @Test
	 public void testShouldDeleteAPost() throws Exception {
		 List<Post> posts = service.getPostsByUser("secondUser");
		 Post update = posts.stream().findFirst().get();
		 service.deletePost(update.getPostId());
		 assertThat(service.getPostsByUser("secondUser"), hasSize(0) );
	 }


	@Test
	public void testShouldFindTextinPosts(){
	 	//search for a word not available in any post
		assertThat(service.fullTextPostSearch("lemon"), hasSize(0));
		assertThat(service.fullTextPostSearch("another"), hasSize(1));
	}


	@Test
	public void testShouldSearchPostByUserAndTime() throws Exception{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSX");
		String formatted = df.format(new Date());

		assertThat(service.searchPostsByUserAndTime("newuser",
				formatted) ,hasSize(0));
	}

	@Test
	public void testShouldSearchPostByUserAndNoTime() throws Exception{
		  assertThat(service.searchPostsByUserAndTime("newuser",null) ,hasSize(2));
	}

	@Test
	public void testShouldSearchPostByTimeAndNoUser() throws Exception{
		assertThat(service.searchPostsByUserAndTime(null,"2012-10-01T09:45:00.000+02:00") ,hasSize(4));

	}


	@Test
	public void testShouldNotAccessPostsWithNoAuth() throws Exception {
		this.mockMvc.perform(get("/villains/posts")).andDo(print()).andExpect(status().isUnauthorized());

	}


	@Test
	@WithMockUser(roles="ADMIN")
	public void testShouldGETPostsWithAuth() throws Exception {
		this.mockMvc.perform(get("/villains/posts")).andDo(print()).andExpect(status().isOk());

		mockMvc.perform(get("/villains/posts")
		).andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$[0].body").exists())
				.andExpect(jsonPath("$[0].body").value("This is a first post"));
	}






	@Test
	@WithMockUser(roles="ADMIN")
	public void testShouldGETPostsByUserWithAuth() throws Exception {
		this.mockMvc.perform(get("/villains/posts/user/{userId}", "secondUser")).andDo(print()).andExpect(status().isOk());

		this.mockMvc.perform(get("/villains/posts/user/{userId}", "secondUser")
		).andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$[0].userId").exists())
				.andExpect(jsonPath("$[0].userId").value("secondUser"))
				.andExpect(jsonPath("$[0].body").value("This is a second post"));

	}

	@Test
	@WithMockUser(roles="ADMIN")
	public void testShouldNotGETPostsByUnknownUserWithAuth() throws Exception {
		this.mockMvc.perform(get("/villains/posts/?userId=someone")).andDo(print()).andExpect(status().isNotFound());

	}

	@Test
	@WithMockUser(roles="ADMIN")
	public void testShouldNotDELETEPostByUnknownPostIdWithAuth() throws Exception {
		this.mockMvc.perform(delete("/villains/posts/{id}", "1234")).andDo(print()).andExpect(status().isNotFound());

	}





	private List<Post> createPosts(){
		List<Post> posts = new ArrayList<>();

		Post firstPost = new Post();
		firstPost.setBody("This is a first post");
		firstPost.setPostTimeStamp(System.currentTimeMillis());
		firstPost.setRating("Anotheuser", true);
		firstPost.setUserId("newuser");


		Post secondPost = new Post();
		secondPost.setBody("This is a second post");
		secondPost.setPostTimeStamp(System.currentTimeMillis());
		secondPost.setRating(null,true);
		secondPost.setUserId("secondUser");


		Post thirdPost = new Post();
		thirdPost.setBody("This is post number 3 ");
		thirdPost.setPostTimeStamp(System.currentTimeMillis());
		thirdPost.setRating("newuser", false);

		Post anotherPost = new Post();
		anotherPost.setBody("another post");
		anotherPost.setPostTimeStamp(System.currentTimeMillis());
		anotherPost.setUserId("newuser");


		posts.add(firstPost);
		posts.add(secondPost);
		posts.add(thirdPost);
		posts.add(anotherPost);
		return posts;

	}

}
