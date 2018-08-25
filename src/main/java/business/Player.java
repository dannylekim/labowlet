package business;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Player {
    private String name;
    private String id;

    public Player(String name){
        this.name = name;
        this.id = UUID.randomUUID().toString();
    }

    // ===================== Getters ======================= //

    public String getName() {
        return name;
    }


    public String getId(){
        return id;
    }


}
