package ua.oschadbank.sender;


import java.util.List;

public class Client {
    private String name;
    private boolean active;
    private List<Direction> directions;
    
    Client(String name, List<Direction> directions, boolean active) {
        this.name = name;        
        this.directions = directions;
        this.active = active;
    }
    
    public List<Direction> getDirections() {
        return directions;
    }
}
