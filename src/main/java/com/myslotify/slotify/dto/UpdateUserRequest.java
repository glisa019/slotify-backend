package com.myslotify.slotify.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    public String firstName;
    public String lastName;
    public String phone;
    public String email;
}
