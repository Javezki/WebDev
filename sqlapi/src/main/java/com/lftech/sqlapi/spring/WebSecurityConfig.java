package com.lftech.sqlapi.spring;

import com.lftech.sqlapi.exception.ExUtils;
import com.lftech.sqlapi.pojo.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

@Configuration
public class WebSecurityConfig {

    JsonLoginWebFilter loginWebFilter;

    public WebSecurityConfig(JsonLoginWebFilter loginWebFilter) {
        this.loginWebFilter = loginWebFilter;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .logout().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and().authorizeHttpRequests((authz) -> authz
                .antMatchers("/", "/static/**").permitAll()
                .anyRequest().authenticated())
                .cors(c -> c.configurationSource(corsConfigurationSource())).csrf().disable()
                .headers().frameOptions().sameOrigin()
                .and().formLogin()
                .and().addFilterBefore(loginWebFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(bearerAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private AuthenticationFilter bearerAuthenticationFilter() {
        AuthenticationFilter filter = new AuthenticationFilter(tokenAuthenticationManager(), tokenAuthenticationConverter());
        filter.setSuccessHandler((request, response, authentication) -> { });
        return filter;
    }

    private AuthenticationManager tokenAuthenticationManager() {

        return authentication -> {

            String token = (String) authentication.getCredentials();
            User user = Jwt.parseAutToken(token.substring(Jwt.TOKEN_PREFIX_LENGTH));
            if (user.getRoles().size() == 0) {
                throw new AccessDeniedException(ExUtils.getMessage("com.lftech.notRoles"));
            }
            //TODO 添加session管理
            return new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());
        };
    }

    private AuthenticationConverter tokenAuthenticationConverter() {

        return request -> {

            String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (authorization == null || !authorization.startsWith(Jwt.TOKEN_PREFIX))
                return null;

            return new UsernamePasswordAuthenticationToken(null, authorization.substring(Jwt.TOKEN_PREFIX_LENGTH));
        };
    }

//    @Bean
//    public InMemoryUserDetailsManager userDetailsService() {
//        UserDetails user = org.springframework.security.core.userdetails.User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
//        return new InMemoryUserDetailsManager(user);
//    }
}
