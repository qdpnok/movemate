package com.human.movemate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Crew {
    private int id;
    private String name;
    private String area;
    private int memberCount;
    private int maxCount;
}

