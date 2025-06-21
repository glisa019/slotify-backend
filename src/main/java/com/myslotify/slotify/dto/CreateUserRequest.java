package com.myslotify.slotify.dto;

import com.myslotify.slotify.entity.Role;
import lombok.Data;

@Data
public class CreateUserRequest {
    public String firstName;
    public String lastName;
    public String phone;
    public String email;
    public String password;
    public String role;
}
