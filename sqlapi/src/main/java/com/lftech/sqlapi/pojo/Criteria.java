package com.lftech.sqlapi.pojo;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Criteria {
    private String username;
    private Long id;
}
