package com.myslotify.slotify.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class CreateAvailabilityRequest {
    private List<LocalDate> dates;
    private LocalTime shiftStart;
    private LocalTime shiftEnd;
}
