package com.jessealves.todosimples.models.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.jessealves.todosimples.models.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserCreateDTO {

    @NotBlank
    @Size(min = 2, max = 100)
    private String username;

    @NotBlank
    @Size(min = 8, max = 60)
    private String password;

    public User toEntity() {
        User user = new User();

        user.setUsername(this.username);
        user.setPassword(this.password);

        return user;
    }
    
}
