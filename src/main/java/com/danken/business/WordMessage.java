package com.danken.business;

import lombok.Data;

@Data
public class WordMessage {

    private final String word;
    private final int remainingWordCount;
}
