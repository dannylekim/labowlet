package com.danken.business;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
@NoArgsConstructor
public class Team {

    private String teamName;

    public static final int MAX_TEAM_MEMBERS = 2;

    private List<Player> teamMembers;

    private Score teamScore;

    private String teamId;

    public Team(String teamName) {
        this.teamName = teamName;
        this.teamScore = new Score();
        this.teamId = UUID.randomUUID().toString();
        this.teamMembers = new ArrayList<>();
        log.info("Created " + teamName + " with id " + this.teamId);
    }

    public boolean isPlayerInTeam(Player player) {
        return teamMembers.contains(player);
    }

    /***
     * Returns true if the player was found and removed.
     *
     * @param player the player to remove from the team
     * @return boolean value if the removal was successful
     */
    public boolean removePlayerFromTeam(Player player) {
        log.info("Removing player {}", player.getName());

        boolean isRemoved = teamMembers.remove(player);
        if (teamMembers.isEmpty()) {
            teamName = "Empty Slot";
        }

        return isRemoved;

    }

    /***
     *  Returns true if the player has joined the team.
     *
     * @param teamMember the player to join this team
     * @return true if joined
     */
    public boolean addPlayerInTeam(Player teamMember) {
        if (teamMembers.size() >= MAX_TEAM_MEMBERS) {
            return false;
        }

        if (!teamMembers.contains(teamMember)) {
            log.info("Team member {} has been added", teamMember.getName());
            return teamMembers.add(teamMember);
        }

        return false;

    }

    public boolean isEmpty() {
        return teamMembers.size() == 0;
    }

}
