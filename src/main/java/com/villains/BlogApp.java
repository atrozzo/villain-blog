package com.villains;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.undertow.UndertowBuilderCustomizer;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class BlogApp {


	public static void main(String[] args) {
		SpringApplication.run(BlogApp.class, args);
	}

	private @Value("${server.undertow.port}") String serverPort;
	private @Value("${server.undertow.host}") String serverHost;

	@Bean
	public UndertowEmbeddedServletContainerFactory embeddedServletContainerFactory() {
		UndertowEmbeddedServletContainerFactory factory =
				new UndertowEmbeddedServletContainerFactory();

		factory.addBuilderCustomizers(new UndertowBuilderCustomizer() {
			@Override
			public void customize(io.undertow.Undertow.Builder builder) {
				builder.addHttpListener(Integer.parseInt(serverPort), serverHost);
			}
		});

		return factory;
	}

}
