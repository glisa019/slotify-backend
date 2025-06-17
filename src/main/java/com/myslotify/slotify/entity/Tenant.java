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
    @Column(name = "tenant_id")
    private UUID tenantId;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "schema_name", nullable = false, unique = true)
    private String schemaName;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "subscription_status", nullable = false)
    private SubscriptionStatus subscriptionStatus;
  
    @OneToOne
    @JoinColumn(name = "admin_id")
    private Admin tenantAdmin;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "motto")
    private String motto;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

    @Column(name = "tiktok")
    private String tiktok;

    @Column(name = "instagram")
    private String instagram;

    @Column(name = "text_colour")
    private String textColour;

    @Column(name = "background_colour")
    private String backgroundColour;

    @Column(name = "border_colour")
    private String borderColour;

    @Column(name = "font")
    private String font;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "cover_picture_url")
    private String coverPictureUrl;
}
