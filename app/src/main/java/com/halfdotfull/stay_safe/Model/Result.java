package com.halfdotfull.stay_safe.Model;

/**
 * Created by sushmita raj on 11/02/2019.
 */

public class Result {

    String name;
    String place_id;

    public String getPlace_id() {
        return place_id;
    }

    public Result(String name, String place_id) {
        this.name = name;
        this.place_id = place_id;
    }

    public String getName() {
        return name;
    }
}
