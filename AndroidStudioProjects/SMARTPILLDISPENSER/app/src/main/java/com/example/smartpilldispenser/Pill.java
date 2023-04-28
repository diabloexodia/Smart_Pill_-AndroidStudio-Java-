package com.example.smartpilldispenser;

import java.util.ArrayList;

public class Pill {

    String PillName;
    ArrayList<String> time;
    int PillDosage;

    Pill(String PillName, int PillDosage) {
        time = new ArrayList<>();
        this.PillName = PillName;
        this.PillDosage = PillDosage;

    }

    public String getName() {
        return PillName;
    }

    public int getPillDosage() {
        return PillDosage;
    }


//    public void setTime(String currentTime) {
//        time.add(currentTime);
//    }

//    public ArrayList<String> getTime() {
//        return time;
//    }
}
