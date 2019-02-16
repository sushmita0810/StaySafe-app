package com.halfdotfull.stay_safe.Model;

import java.util.ArrayList;

/**
 * Created by sushmita raj on 09/02/2019.
 */

public class Request {
    ArrayList<Result> results;

    public Request(ArrayList<Result> results) {
        this.results = results;
    }

    public ArrayList<Result> getResults() {
        return results;
    }
}
