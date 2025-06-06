package com.myslotify.slotify.dto;

import com.myslotify.slotify.entity.Interval;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateServiceRequest {
    private String name;
    private Interval duration;
    private String description;
    private BigDecimal price;
}
