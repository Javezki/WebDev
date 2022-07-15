package com.lftech.sqlapi;

import com.lftech.sqlapi.database.ConnectorFactory;
import com.lftech.sqlapi.pojo.ConnectionConfig;
import com.lftech.sqlapi.pojo.Criteria;
import com.lftech.sqlapi.pojo.Query;
import com.lftech.sqlapi.pojo.User;
import com.lftech.sqlapi.repository.ConnectionRepository;
import com.lftech.sqlapi.repository.QueryRepository;
import com.lftech.sqlapi.spring.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
public class SqlApiController {

    private ConnectionRepository connectionRepository;

    private QueryRepository queryRepository;

    private ConnectorFactory connectorFactory;

    private ApplicationContext context;

    @Autowired
    SqlApiController(ConnectionRepository connectionRepository, QueryRepository queryRepository,
                     ConnectorFactory connectorFactory,
                     ApplicationContext context) {
        this.connectionRepository = connectionRepository;
        this.queryRepository = queryRepository;
        this.connectorFactory = connectorFactory;
        this.context = context;
    }

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

    @GetMapping("/connections")
    public ResponseEntity<Object> connections() {
        return ResponseEntity.ok().body(connectionRepository.listConnections());
    }

    @GetMapping("/queries")
    public ResponseEntity<Object> queries() {
        return ResponseEntity.ok().body(queryRepository.listQueries());
    }

    @GetMapping("/{queryName}/sql")
    public ResponseEntity<Object> connect(@PathVariable String queryName, @RequestParam(required = false)  MultiValueMap<String, String> requestParams) throws Exception {
        Query con = queryRepository.findByQueryName(queryName);
        NamedParameterJdbcTemplate jt = new NamedParameterJdbcTemplate(connectorFactory.connect(con));
        List<Map<String, Object>> result = jt.queryForList(con.getSql(),requestParams);
        return ResponseEntity.ok()
                .body(result);
    }

    @GetMapping("/connect")
    public String connect() throws Exception {
        for ( ConnectionConfig con : connectionRepository.listConnections()) {
            connectorFactory.connect(con).getConnection();
        }
        return "Success";
    }

    @GetMapping("/criteria")
    public ResponseEntity<Object> criteria(Map<String, String> requestParams) {
        Long id = null;
        if (requestParams.containsKey("id")) {
            id = Long.valueOf(requestParams.get("id"));
        }
        context.getBean(Criteria.class).setId(id);
        return ResponseEntity.ok().body(connectionRepository.listById(id));
    }
}
