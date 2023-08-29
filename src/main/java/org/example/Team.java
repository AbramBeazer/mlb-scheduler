package org.example;

import java.time.LocalDate;
import java.util.Objects;

public class Team {

    private String location;
    private String name;
    private TeamCode code;
    private TeamCode rival;

    private LocalDate mostRecentGame;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TeamCode getCode() {
        return code;
    }

    public void setCode(TeamCode code) {
        this.code = code;
    }

    public TeamCode getRival() {
        return rival;
    }

    public void setRival(TeamCode rival) {
        this.rival = rival;
    }

    public LocalDate getMostRecentGame() {
        return mostRecentGame;
    }

    public void setMostRecentGame(LocalDate mostRecentGame) {
        this.mostRecentGame = mostRecentGame;
    }

    @Override
    public String toString() {
        return location + " " + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Team team = (Team) o;
        return location.equals(team.location) && name.equals(team.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, name);
    }
}
