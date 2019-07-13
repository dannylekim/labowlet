package com.danken.application.controllers;

import java.util.List;

import com.danken.business.WordBowlInputState;
import com.danken.utility.SocketSessionUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.extern.slf4j.Slf4j;


@Controller
@Slf4j
public class WordBowlController {

    @MessageMapping("/room/{code}/addWords")
    @SendTo("client/room/{code}/addWords")
    public WordBowlInputState addWords(@RequestBody List<String> inputWords, SimpMessageHeaderAccessor accessor) {
        var currentRoom = SocketSessionUtils.getRoom(accessor);
        var game = currentRoom.getGame();

        if (game == null) {
            throw new IllegalStateException("Game hasn't started yet");
        }

        game.addWordBowl(inputWords, SocketSessionUtils.getSession(accessor).getPlayer());
        return game.getState();
    }


}
