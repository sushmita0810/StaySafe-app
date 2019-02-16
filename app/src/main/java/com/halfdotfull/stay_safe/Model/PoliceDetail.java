package com.halfdotfull.stay_safe.Model;

/**
 * Created by sushmita raj on 08/02/2019.
 */

public class PoliceDetail {

    String policeStation;
    String number;

    public PoliceDetail(String policeStation, String number) {
        this.policeStation = policeStation;
        this.number = number;
    }

    public String getPoliceStation() {
        return policeStation;
    }

    public void setPoliceStation(String policeStation) {
        this.policeStation = policeStation;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
