package com.danken.business;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class Player {

    @Setter
    private String name;

    private String id;

    @Setter
    private int uniqueIconReference;

    public Player() {
        this.id = UUID.randomUUID().toString();
    }
}
