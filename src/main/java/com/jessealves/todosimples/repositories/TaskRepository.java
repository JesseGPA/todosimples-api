package com.jessealves.todosimples.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jessealves.todosimples.models.Task;
import com.jessealves.todosimples.models.projection.TaskProjection;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<TaskProjection> findByUser_Id(Long id);

}
