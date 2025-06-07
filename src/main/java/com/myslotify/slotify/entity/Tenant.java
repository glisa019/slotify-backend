package com.myslotify.slotify.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tenant", schema = "system")
@Data
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID tenantId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String schemaName;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private SubscriptionStatus subscriptionStatus;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User tenantAdmin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String motto;
    private String address;
    private String phone;
    private String tiktok;
    private String instagram;
    private String textColour;
    private String backgroundColour;
    private String borderColour;
    private String font;

    @Lob
    private byte[] logo;
    @Lob
    private byte[] coverPicture;
}
