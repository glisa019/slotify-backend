package com.myslotify.slotify.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
public class User extends BaseAccount {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean passwordResetRequired = false;
}
