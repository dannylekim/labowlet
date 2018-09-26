package com.danken.business;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Getter
@NoArgsConstructor
@Slf4j
public class Player {
    private String name;
    private String id;

    public Player(String name){
        this.name = name;
        this.id = UUID.randomUUID().toString();
        log.info("Created player with {} and id {}", name , id);
    }
}
