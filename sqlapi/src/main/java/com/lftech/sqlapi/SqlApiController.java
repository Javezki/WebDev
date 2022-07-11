package com.lftech.sqlapi;

import com.lftech.sqlapi.pojo.User;
import com.lftech.sqlapi.spring.Jwt;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class SqlApiController {
    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<Object> login(HttpServletResponse response) {
        User p = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HttpHeaders responseHeaders = new HttpHeaders();
        String token = Jwt.createAutToken(p);
        responseHeaders.set(HttpHeaders.AUTHORIZATION, token);
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(Jwt.parseToken(token.substring(Jwt.TOKEN_PREFIX_LENGTH), Jwt.AUTH_AUDIENCE).getClaims());
    }

    @GetMapping("/testing")
    public String testing() {
        return "testing";
    }
}
