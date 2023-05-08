package com.jessealves.todosimples.controllers;

import java.net.URI;
import java.util.List;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.jessealves.todosimples.models.Task;
import com.jessealves.todosimples.models.projection.TaskProjection;
import com.jessealves.todosimples.services.TaskService;

@RestController
@RequestMapping("/tasks")
@Validated
public class TaskController {
    
    @Autowired
    private TaskService taskService;

    @GetMapping("/{id}")
    public ResponseEntity<Task> findById(@PathVariable @Min(1) Long id) {
        Task task = this.taskService.findById(id);
        return ResponseEntity.ok().body(task);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<Task>> findAll() {
        List<Task> tasks = this.taskService.findAll();
        return ResponseEntity.ok().body(tasks);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskProjection>> findAllByUser(@PathVariable @Min(1) Long userId) {
        List<TaskProjection> tasks = this.taskService.findAllByUser(userId);
        return ResponseEntity.ok().body(tasks);
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody Task task) {
        this.taskService.create(task);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(task.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@Valid @RequestBody Task task, @PathVariable @Min(1) Long id) {
        if (!id.equals(task.getId())) {
            throw new ConstraintViolationException("O id informado no parâmetro da requisição não corresponde com o id informado no corpo da requisição.", null);
        }
        this.taskService.update(task);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Min(1) Long id) {
        this.taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
