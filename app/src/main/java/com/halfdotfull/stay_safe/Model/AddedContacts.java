package com.halfdotfull.stay_safe.Model;
public class AddedContacts {

    String nameAdded;
    String numberAdded;

    public AddedContacts(String numberAdded, String nameAdded) {
        this.nameAdded = nameAdded;
        this.numberAdded = numberAdded;
    }

    public String getNameAdded() {
        return nameAdded;
    }

    public void setNameAdded(String nameAdded) {
        this.nameAdded = nameAdded;
    }

    public String getNumberAdded() {
        return numberAdded;
    }

    public void setNumberAdded(String numberAdded) {
        this.numberAdded = numberAdded;
    }
}
