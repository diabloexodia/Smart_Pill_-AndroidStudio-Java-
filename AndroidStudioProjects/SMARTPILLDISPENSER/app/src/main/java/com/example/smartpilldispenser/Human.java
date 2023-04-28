package com.example.smartpilldispenser;

import com.example.smartpilldispenser.Pill;

import java.util.ArrayList;

public class Human {
    String HumanName;
    Pill pillObject;

    Human(String HumanName) {
        this.HumanName = HumanName;
    }

//    public void setHumanName(String humanName) {
//        HumanName = humanName;
//    }

    public String getHumanName() {
        return HumanName;
    }

    public void setPillObject(String pillName, int PillDosage) {
        pillObject = new Pill(pillName, PillDosage);
    }
}
