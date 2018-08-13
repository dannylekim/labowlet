package application.controllers;

import application.LabowletState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class RoomController {

   private LabowletState applicationState;

    @Autowired
    HttpSession session;


    public RoomController(){
        applicationState = LabowletState.getInstance();
    }
}
