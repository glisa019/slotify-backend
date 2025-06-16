package com.myslotify.slotify.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "service")
@Data
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "service_id")
    private UUID serviceId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "duration", nullable = false)
    private Interval duration;

    @Column(name = "price", nullable = false)
    private BigDecimal price;
}
