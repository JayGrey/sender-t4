package com.darkbytes.sender;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Client implements Serializable {
    private String name;
    private boolean active;
    private List<Direction> directions;
    
    Client(String name) {
        this(name, new ArrayList<Direction>(), true);
    }
    
    Client(String name, List<Direction> directions, boolean active) {
        this.name = name;        
        this.directions = directions;
        this.active = active;
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
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if ( other == this) {
            return false;
        } else if (other instanceof Client) {
            Client client = (Client) other;
            return client.name.equals(name) && client.active == active && client.directions.equals(directions);
        } else {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return String.format("Client{name: %s}", name);
    }
}
