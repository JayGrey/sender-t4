package com.darkbytes.sender;


import java.util.ArrayList;
import java.util.List;


public class Client {
    private String name;
    private List<Direction> directions;

    Client(String name) {
        this(name, new ArrayList<Direction>());
    }

    Client(String name, List<Direction> directions) {
        this.name = name;
        this.directions = directions;
    }

    public void addDirection(Direction direction) {
        if (directions == null || direction == null) {
            return;
        }
        directions.add(direction);
    }

    public List<Direction> getDirections() {
        return directions;
    }


    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (other == this) {
            return false;
        } else if (other instanceof Client) {
            Client client = (Client) other;
            return client.name.equals(name) && client.directions.equals(directions);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("c (%s)", name);
    }

    public String getName() {
        return name;
    }
}
