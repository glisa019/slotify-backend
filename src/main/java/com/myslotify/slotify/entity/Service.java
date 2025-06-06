package com.myslotify.slotify.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID serviceId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Interval duration;
    @Column(nullable = false)
    private BigDecimal price;
}
