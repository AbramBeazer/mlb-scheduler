package org.example;

import java.time.LocalDate;

public class Game {
    private LocalDate date;
    private Team away;
    private Team home;

    public Game(LocalDate date, Team away, Team home) {
        this.date = date;
        this.away = away;
        this.home = home;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Team getAway() {
        return away;
    }

    public void setAway(Team away) {
        this.away = away;
    }

    public Team getHome() {
        return home;
    }

    public void setHome(Team home) {
        this.home = home;
    }
}
