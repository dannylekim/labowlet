package com.danken.business;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WordBowlInputState {

    List<UserWordBowlStatus> usersStatus = new ArrayList<>();
    boolean isReady = false;

    public WordBowlInputState(List<Player> players){
        players.stream().forEach(player -> {
            var status = new UserWordBowlStatus();
            status.setPlayer(player);
            usersStatus.add(status);
        });
    }

    public boolean isReady() {
        return usersStatus
                .stream()
                .allMatch(UserWordBowlStatus::isCompleted);
    }
}


@Getter
@Setter
class UserWordBowlStatus {
    private Player player;
    private boolean completed = false;
}
