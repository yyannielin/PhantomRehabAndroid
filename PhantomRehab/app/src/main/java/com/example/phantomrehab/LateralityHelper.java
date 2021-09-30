package com.example.phantomrehab;

public class LateralityHelper {

    String time;
    int score;
    int POF;

    public LateralityHelper() {}

    public LateralityHelper(String time, int score, int POF) {
        this.time = time;
        this.score = score;
        this.POF = POF;
    }

    public String getTime() {
        return time;
    }

    public int getScore() {
        return score;
    }

    public int getPOF() {
        return POF;
    }

}
