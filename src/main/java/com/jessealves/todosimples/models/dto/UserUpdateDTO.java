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
public class UserUpdateDTO {

    private Long id;

    @NotBlank
    @Size(min = 8, max = 60)
    private String password;

    public User toEntity() {
        User user = new User();

        user.setId(this.id);
        user.setPassword(this.password);

        return user;
    }
    
}
