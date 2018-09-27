package com.danken.business;

import lombok.Data;
import lombok.NonNull;

@Data
public class OutputMessage {

    @NonNull
    private String id;
    @NonNull
    private Object payload;
}
