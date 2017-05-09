package com.villains.conf.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class BlogSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    private BlogAuthentication secEntryPoint;

    @Autowired
    private BlogSavedRequestAwareAuthSuccessHandler authenticationSuccessHandler;

    @Override
   protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {

        auth.inMemoryAuthentication()  // this can be replaced wiht encripted password saved in the system or DB.
                // this is just a prototype.
                .withUser("admin").password("password").roles("ADMIN")
                .and()
                .withUser("user").password("password").roles("USER", "ADMIN");
    }

   @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().and()
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(secEntryPoint)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/villains").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/villains*//**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PATCH, "/villains*//**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/villains*//**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/villains/swagger-ui.html").permitAll()
                .and()
                .formLogin()
                .successHandler(authenticationSuccessHandler)
                .failureHandler(new SimpleUrlAuthenticationFailureHandler())
                .and()
                .logout();
    }

    @Bean
    public BlogSavedRequestAwareAuthSuccessHandler mySuccessHandler(){
        return new BlogSavedRequestAwareAuthSuccessHandler();
    }
   @Bean
    public SimpleUrlAuthenticationFailureHandler myFailureHandler(){
        return new SimpleUrlAuthenticationFailureHandler();
    }

}
