package com.danken.business;

import lombok.Data;
import lombok.NonNull;

@Data
public class OutputMessage {

    public static String ROOM_EVENT = "ROOM";

    @NonNull
    private String id;
    @NonNull
    private Object payload;
}
