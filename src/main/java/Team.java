public class Team {
    private String teamName;
    private Player teamMember1;
    private Player teamMember2;
    private Score teamScore;

    public Team(String teamName, Player teamMember1, Player teamMember2){
        this.teamName = teamName;
        this.teamMember1 = teamMember1;
        this.teamMember2 = teamMember2;
        this.teamScore = new Score();
    }

    public String getTeamName(){
        return this.teamName;
    }

    public void setTeamName(String teamName){
        this.teamName = teamName;
    }

    public Player getTeamMember1(){
        return teamMember1;
    }

    public Player getTeamMember2(){
        return teamMember2;
    }

    public void setTeamMember1(Player teamMember1){
        if(this.teamMember1 != teamMember1 && this.teamMember2 != teamMember1) {
            this.teamMember1 = teamMember1;
        }
        //todo: handle error otherwise
    }

    public void setTeamMember2(Player teamMember2){
        if(this.teamMember1 != teamMember2 && this.teamMember2 != teamMember2) {
            this.teamMember2 = teamMember2;
        }
        //todo: handle error otherwise
    }


}
