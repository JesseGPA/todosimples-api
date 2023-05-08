package com.jessealves.todosimples.services;

import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jessealves.todosimples.models.Task;
import com.jessealves.todosimples.models.User;
import com.jessealves.todosimples.repositories.UserRepository;
import com.jessealves.todosimples.security.UserSpringSecurity;
import com.jessealves.todosimples.services.exceptions.ObjectNotFoundException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findLoggedUser() {
        Long id = ((UserSpringSecurity) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        User user = findById(id);

        return user;
    }

    public User findById(Long id) {
        Optional<User> user = this.userRepository.findById(id);

        return user.orElseThrow(() -> new ObjectNotFoundException(
            "Usuário não encontrado! Id: " + id
        ));
    }

    @Transactional
    public User create(User obj) {
        obj = this.userRepository.save(obj);

        return obj;
    }

    @Transactional
    public User update(User obj) {
        User newObj = findById(obj.getId());
        
        newObj.setPassword(encodePassword(obj.getPassword()));

        return this.userRepository.save(newObj);
    }
    
    @Transactional
    public void delete(Long id) {
        User user = findById(id);
        List<Task> tasks = this.userRepository.findTasksByUserId(user);
        
        if (!tasks.isEmpty())
            throw new ConstraintViolationException("O usuário " + id + " possui tarefas relacionadas.", null);

        this.userRepository.deleteById(id);
    }

    public static String encodePassword(String password) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(password);
    }
}
