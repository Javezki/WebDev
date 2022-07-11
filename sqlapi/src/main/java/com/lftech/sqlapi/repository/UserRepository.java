package com.lftech.sqlapi.repository;

import com.lftech.sqlapi.pojo.User;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
    @Query("SELECT * FROM sqlapi_user WHERE id = :id")
    User retriveById(Integer id);

    @Query("SELECT * FROM sqlapi_user")
    List<User> listUsers();

    @Query("SELECT * FROM sqlapi_user WHERE username = :username")
    User findByUsername(String username);
}
