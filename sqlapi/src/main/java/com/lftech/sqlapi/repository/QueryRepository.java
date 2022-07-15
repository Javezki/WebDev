package com.lftech.sqlapi.repository;

import com.lftech.sqlapi.pojo.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface QueryRepository extends CrudRepository<Query, Long> {
    @org.springframework.data.jdbc.repository.query.Query("SELECT b.*, a.query_name, a.sql, a.id AS query_id FROM sqlapi_query a JOIN sqlapi_connection b ON a.connection_id = b.id WHERE query_name = :queryName")
    Query findByQueryName(String queryName);

    @org.springframework.data.jdbc.repository.query.Query("SELECT b.*, a.query_name, a.sql, a.id AS query_id FROM sqlapi_query a JOIN sqlapi_connection b ON a.connection_id = b.id ")
    List<Query> listQueries();
}
