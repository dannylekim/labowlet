package application.controllers;

import application.LabowletState;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoomController {

   private LabowletState applicationState;

    public RoomController(){
        applicationState = LabowletState.getInstance();
    }
}
