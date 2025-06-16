package com.myslotify.slotify.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "user")
@Data
@EqualsAndHashCode(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends BaseAccount {

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "password_reset_required", nullable = false)
    private boolean passwordResetRequired = false;
}
