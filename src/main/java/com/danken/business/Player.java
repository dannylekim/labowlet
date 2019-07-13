package com.danken.business;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Getter
@Slf4j
public class Player {
    @Setter
    private String name;
    private String id;

    public Player(){
        this.id = UUID.randomUUID().toString();
    }
}
