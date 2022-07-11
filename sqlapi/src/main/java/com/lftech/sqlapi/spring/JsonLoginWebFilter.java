package com.lftech.sqlapi.spring;

import com.lftech.sqlapi.Utils.JsonUtils;
import com.lftech.sqlapi.Utils.Snowflake;
import com.lftech.sqlapi.exception.ExUtils;
import com.lftech.sqlapi.pojo.User;
import com.lftech.sqlapi.repository.UserRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.ForwardAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class JsonLoginWebFilter extends AbstractAuthenticationProcessingFilter {

    protected JsonLoginWebFilter(LoginUserDetailsService customUserDetailsService) {
        super(new AntPathRequestMatcher("/login", "POST"));
        setAuthenticationManager(new LoginAuthenticationManager(customUserDetailsService));
        setAuthenticationSuccessHandler(new ForwardAuthenticationSuccessHandler("/login"));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request.getContentType() == null) {
            chain.doFilter(request, response);
            return;
        }
        MediaType contentType = MediaType.parseMediaType(request.getContentType());
        if (!MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            chain.doFilter(request, response);
            return;
        }
        super.doFilter(request, response,chain);
    }
    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {

        String username, password;

        Map<String, String> requestMap = JsonUtils.objectMapper.readValue(request.getInputStream(), Map.class);
        username = requestMap.get("username");
        password = requestMap.get("password");

        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username, password);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    private class LoginAuthenticationManager implements AuthenticationManager {

        LoginAuthenticationManager (LoginUserDetailsService customUserDetailsService ){
            this.customUserDetailsService = customUserDetailsService;
        }

        @Autowired
        private LoginUserDetailsService customUserDetailsService;

        public Authentication authenticate(Authentication authentication) {
            User userDetail = (User)  customUserDetailsService.loadUserByUsername(authentication.getName());
            userDetail.eraseCredentials();
            return new UsernamePasswordAuthenticationToken(userDetail, userDetail.getPassword(), userDetail.getAuthorities());
        }
    }
    @Component
    static class LoginUserDetailsService implements UserDetailsService {

        private UserRepository userRepository;
        private Snowflake snowflake;

        public LoginUserDetailsService(UserRepository userRepository, Snowflake snowflake) {
            this.userRepository = userRepository;
            this.snowflake = snowflake;
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            //TODO 添加session管理
            User u = userRepository.findByUsername(username);
            if (u == null) {
                throw new UsernameNotFoundException(username);
            }
            u.setSessionId(snowflake.nextId());
            return u;
        }
    }

    @Component
    static class JsonAuthenticationConverter implements AuthenticationConverter {

        private final List<HttpMessageConverter<?>> httpMessageReaders;

        public JsonAuthenticationConverter(List<HttpMessageConverter<?>> httpMessageReaders) {
            this.httpMessageReaders = Objects.requireNonNull(httpMessageReaders);
        }

        @Override
        public Authentication convert(HttpServletRequest request) {
            MediaType contentType = MediaType.parseMediaType(request.getContentType());
            if (contentType == null || !MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
                throw  new BadCredentialsException("Invalid Content-Type");
            }
            User user = null;
            try {
                user = ServerRequest.create(request,this.httpMessageReaders).body(User.class);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

        }
    }
}


