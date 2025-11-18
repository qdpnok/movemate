package com.human.movemate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mate {
    private int id;
    private String name;
    private String area;   // 천안 동네명
}

