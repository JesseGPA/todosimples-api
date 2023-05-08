package com.jessealves.todosimples.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jessealves.todosimples.models.Task;
import com.jessealves.todosimples.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    
    User findByUsername(String username);

    @Query(value = "select t from Task t where t.user = ?1")
    List<Task> findTasksByUserId(User user);

}
