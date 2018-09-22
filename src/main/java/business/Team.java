package business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class Team {
    private String teamName;
    private Player teamMember1;
    private Player teamMember2;
    private Score teamScore;
    private String teamId;

    private static final Logger logger = LoggerFactory.getLogger(Team.class);

    public Team(String teamName, Player teamMember1) {
        this.teamName = teamName;
        this.teamMember1 = teamMember1;
        this.teamScore = new Score();
        this.teamId = UUID.randomUUID().toString();

        logger.info("Created " + teamName + " with id " + this.teamId);
    }

    public Team(){}

    public String getTeamId() {
        return this.teamId;
    }

    public String getTeamName() {
        return this.teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Player getTeamMember1() {
        return teamMember1;
    }

    public Player getTeamMember2() {
        return teamMember2;
    }

    /***
     * Set the first team member however, it will only set if and only if: the value you're trying to set is null,
     * the value other team member is null or that the value you're setting isn't the same as the other member.
     *
     *
     * @param teamMember1 the player to be set as the first teammember
     */
    public void setTeamMember1(Player teamMember1) {
        logger.debug("Attempting to set teamMember with " + ((teamMember1 == null) ? "null": teamMember1.getName()));
        if (teamMember1 == null || this.teamMember2 == null || (teamMember1 != this.teamMember2)) {
            this.teamMember1 = teamMember1;
            logger.debug("Player " + ((teamMember1 == null) ? "null": teamMember1.getName()) + " is set as Team member 1");
        }
    }

    /***
     * Set the first team member however, it will only set if and only if: the value you're trying to set is null,
     * the value other team member is null or that the value you're setting isn't the same as the other member.
     *
     *
     * @param teamMember2 the player to be set as the first teammember
     */
    public void setTeamMember2(Player teamMember2) {
        logger.debug("Attempting to set teamMember with " + ((teamMember2 == null) ? "null": teamMember2.getName()));

        if (teamMember2 == null || this.teamMember1 == null || (teamMember2 != this.teamMember1)) {
            this.teamMember2 = teamMember2;
            logger.debug("Player " + ((teamMember2 == null) ? "null": teamMember2.getName()) + " is set as Team member 2");

        }

    }

    public boolean isPlayerInTeam(Player player) {
        return (this.teamMember1 == player || this.teamMember2 == player);
    }

    /***
     * Returns true if the player was found and removed.
     *
     * @param player the player to remove from the team
     * @return boolean value if the removal was successful
     */
    public boolean removePlayerFromTeam(Player player) {
        logger.info("Removing player " + player.getName());
        boolean isRemoved = false;
        if (teamMember2 == player) {
            this.setTeamMember2(null);

            logger.debug("Team member 2 has been removed.");
            isRemoved = true;
        }
        if (teamMember1 == player) {
            this.setTeamMember1(null);

            logger.debug("Team member 1 has been removed.");
            isRemoved = true;
        }


        logger.info("Player has been removed: " + isRemoved);
        return isRemoved;

    }

    /***
     *  Returns true if the player has joined the team.
     *
     * @param player the player to join this team
     * @return true if joined
     */
    public boolean addPlayerInTeam(Player player){

        logger.info("Attempting to add player" + player.getName() + " to team");
        boolean hasPlayerJoinedTeam = false;
        if (teamMember1 == null) {
            setTeamMember1(player);
            hasPlayerJoinedTeam = true;
            logger.debug("Team member 1 has been added.");

        } else if (teamMember2 == null) {
            setTeamMember2(player);
            logger.debug("Team member 2 has been added.");
            hasPlayerJoinedTeam = true;
        }

        logger.info("Player has been added: " + hasPlayerJoinedTeam);

        return hasPlayerJoinedTeam;
    }


}
