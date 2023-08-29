package org.example;

import java.util.List;
import java.util.Objects;

public class Division {

    private String name;
    private List<Team> teams;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Division division = (Division) o;
        return name.equals(division.name) && teams.equals(division.teams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, teams);
    }
}
