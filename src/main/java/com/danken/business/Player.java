package com.danken.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class Player {
    private String name;
    private String id;
    private static final Logger logger = LoggerFactory.getLogger(Player.class);

    public Player(String name){
        this.name = name;
        this.id = UUID.randomUUID().toString();

        logger.info("Created player with {} and id {}", name , id);
    }

    public Player(){}

    // ===================== Getters ======================= //

    public String getName() {
        return name;
    }
    public String getId(){
        return id;
    }
    public void setName(String name) {
        this.name = name;
    }
}
