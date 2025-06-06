package com.myslotify.slotify.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "admin_id"))
public class Admin extends BaseAccount {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdminRole role;
}
