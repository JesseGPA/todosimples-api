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

import com.jessealves.todosimples.models.User;
import com.jessealves.todosimples.models.dto.UserCreateDTO;
import com.jessealves.todosimples.models.dto.UserUpdateDTO;
import com.jessealves.todosimples.models.projection.TaskProjection;
import com.jessealves.todosimples.services.TaskService;
import com.jessealves.todosimples.services.UserService;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;
    
    @GetMapping("/me")
    public ResponseEntity<User> findMyUser() {
        User user = this.userService.findLoggedUser();
        return ResponseEntity.ok().body(user);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable @Min(1) Long id) {
        User user = this.userService.findById(id);
        return ResponseEntity.ok().body(user);
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody UserCreateDTO obj) {
        User user = obj.toEntity();
        User newUser = this.userService.create(user);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newUser.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@Valid @RequestBody UserUpdateDTO obj, @PathVariable @Min(1) Long id) {
        if (!id.equals(obj.getId())) {
            throw new ConstraintViolationException("O id informado no parâmetro da requisição não corresponde com o id informado no corpo da requisição.", null);
        }
        User user = obj.toEntity();
        this.userService.update(user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Min(1) Long id) {
        this.userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/myTasks")
    public ResponseEntity<List<TaskProjection>> findAllTasksByUser() {
        List<TaskProjection> tasks = this.taskService.findAllByLoggedUser();
        return ResponseEntity.ok().body(tasks);
    }

}
