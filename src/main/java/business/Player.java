package business;

import java.util.UUID;

public class Player {
    private String name;
    private String id;

    public Player(String name){
        this.name = name;
        this.id = UUID.randomUUID().toString();
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
