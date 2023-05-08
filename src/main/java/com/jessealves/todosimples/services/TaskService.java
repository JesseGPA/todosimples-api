package com.jessealves.todosimples.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jessealves.todosimples.models.Task;
import com.jessealves.todosimples.models.User;
import com.jessealves.todosimples.models.enums.ProfileEnum;
import com.jessealves.todosimples.models.projection.TaskProjection;
import com.jessealves.todosimples.repositories.TaskRepository;
import com.jessealves.todosimples.security.UserSpringSecurity;
import com.jessealves.todosimples.services.exceptions.AuthorizationException;
import com.jessealves.todosimples.services.exceptions.ObjectNotFoundException;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public Task findById(Long id) {
        Task task = this.taskRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(
            "Tarefa não encontrada! Id:" + id
        ));

        User user = this.userService.findLoggedUser();
        if (!user.getProfiles().contains(ProfileEnum.ADMIN) && !userHasTask(user, task))
            throw new AuthorizationException("A tarefa " + id + " não pertence ao usuário logado.");

        return task;
    }

    public List<TaskProjection> findAllByLoggedUser() {
        Long userId = ((UserSpringSecurity) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        List<TaskProjection> tasks = findAllByUser(userId);

        return tasks;
    }

    public List<TaskProjection> findAllByUser(Long userId) {
        List<TaskProjection> tasks = this.taskRepository.findByUser_Id(userId);

        return tasks;
    }

    public List<Task> findAll() {
        List<Task> tasks = this.taskRepository.findAll();

        return tasks;
    }

    @Transactional
    public Task create(Task obj) {
        Long userId = ((UserSpringSecurity) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        User user = this.userService.findById(userId);
        
        obj.setUser(user);
        obj = this.taskRepository.save(obj);

        return obj;
    }

    @Transactional
    public Task update(Task obj) {
        Task newObj = findById(obj.getId());
        newObj.setDescription(obj.getDescription());
        
        return this.taskRepository.save(newObj);
    }
    
    @Transactional
    public void delete(Long id) {
        this.taskRepository.deleteById(id);
    }

    private boolean userHasTask(User user, Task task) {
        return task.getUser().getId().equals(user.getId());
    }

}
