package com.service.config.security;

import com.service.core.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final UserService userService;

    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/favicon.ico", "/fullcalendar-5.11.2/**");
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    UserAuthenticationFailureHandler getFailureHandler() {
        return new UserAuthenticationFailureHandler();
    }

    @Bean
    UserAuthenticationSuccessHandler getSuccessHandler() {
        return new UserAuthenticationSuccessHandler("/");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                .invalidateHttpSession(true)
                .and()
                .formLogin()
                .loginPage("/user/login")
                .usernameParameter("email")
                .successHandler(getSuccessHandler())
                .failureHandler(getFailureHandler())
                .permitAll()
                .and()
                .exceptionHandling().accessDeniedPage("/error/denied")
                .and()
                .headers().frameOptions().sameOrigin()
                .and()
                .authorizeRequests().antMatchers(
                        "/",
                        // user
                        "/user/intro", "/user/login",
                        "/user/signup", "/user/signup-complete",
                        "/user/find-info", "/user/find-email", "/user/update/password",
                        "/user/check-id", "/user/check-email", "/user/email-auth",
                        // blog
                        "/blog/{id}",
                        // category
                        "/category/post/**", "/category/post-title/**", "/category/all/{blogId}",
                        // post
                        "/post/all/{blogId}", "/post/{postId}", "/post/search-keyword",
                        "/post/search-rest", "/post/search/{blogId}", "/post/recent/{blogId}", "/post/popular/{blogId}",
                        // tag
                        "/tag/{tagName}",
                        // comment
                        "/comment/upload/comment-thumbnail-image", "/comment/register", "/comment/{postId}/{blogId}",
                        "/comment/update/**", "/comment/authority/{commentId}", "/comment/delete/{commentId}", "/comment/reply/**",
                        // like
                        "/like/**",
                        // error
                        "/error/**",
                        // email
                        "/email/send/**",
                        // notice alarm
                        "/notice/check-alarm", "/notice/read-alarm"
                )
                .permitAll()
                .and()
                .httpBasic().authenticationEntryPoint(new NoPopupBasicAuthenticationEntryPoint());
        /**
         * DELETE, PUT, POST, PATCH csrf 비활성화 (세션 종료에 따른 토큰만료 Request NOT Supported 이슈 방지)
         **/
        http.csrf().ignoringAntMatchers(
                "/user/login", "/user/signup", "/user/update/password", "/user/email-auth",
                "/category/register/{blogId}",
                "/comment/**",
                "/like/**", "/notice/read-alarm");
        super.configure(http);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(getPasswordEncoder());
        super.configure(auth);
    }
}
