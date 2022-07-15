package com.lftech.sqlapi.repository;

import com.lftech.sqlapi.pojo.ConnectionConfig;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ConnectionRepository extends CrudRepository<ConnectionConfig, Long> {

    @Query("SELECT * FROM sqlapi_connection WHERE id = :id")
    ConnectionConfig retriveById(Long id);

    @Query("SELECT * FROM sqlapi_connection")
    List<ConnectionConfig> listConnections();

    @Query("SELECT * FROM sqlapi_connection WHERE 1 = 1 #{Criteria.id != null ? 'AND id = ?#{[0]' : ''} ")
    Iterable<Object> listById(Long id);
}
