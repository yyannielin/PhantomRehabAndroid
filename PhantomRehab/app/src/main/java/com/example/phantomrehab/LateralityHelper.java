package com.example.phantomrehab;

public class LateralityHelper {

    String time;
    int score;

    public LateralityHelper() {}

    public LateralityHelper(String time, int score) {
        this.time = time;
        this.score = score;
    }

    public String getTime() {
        return time;
    }

    public int getScore() {
        return score;
    }
}
