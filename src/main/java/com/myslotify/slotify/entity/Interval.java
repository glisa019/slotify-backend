package com.myslotify.slotify.entity;

import lombok.Getter;

@Getter
public enum Interval {
    MINUTES_15(15),
    MINUTES_30(30),
    MINUTES_45(45),
    MINUTES_60(60),
    MINUTES_75(75),
    MINUTES_90(90),
    MINUTES_105(105),
    MINUTES_120(120),
    MINUTES_135(135),
    MINUTES_150(150),
    MINUTES_165(165),
    MINUTES_180(180),
    MINUTES_195(195),
    MINUTES_210(210),
    MINUTES_225(225),
    MINUTES_240(240);

    // Getter
    private final int minutes;

    // Constructor
    Interval(int minutes) {
        this.minutes = minutes;
    }

}
