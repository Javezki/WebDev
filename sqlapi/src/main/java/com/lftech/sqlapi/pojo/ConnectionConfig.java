package com.lftech.sqlapi.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;

@Data
@RequiredArgsConstructor
@Slf4j
public class ConnectionConfig {
    @Id
    private Long id;
    private transient String driverClassName;
    private transient String url;
    private transient String username;
    @JsonIgnore
    private transient String password;

    public String getCacheKey() {
        return String.format("%s-%s-%s", this.driverClassName, this.url, this.username);
    }}
