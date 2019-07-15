package com.danken.business;

import lombok.Data;

@Data
public class FullGameState {

    private Room room;

    private Player player;

    private Game game;

    private WordBowlInputState wordState;

    private Team team;

    private String currentlyIn;

}
