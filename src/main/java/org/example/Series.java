package org.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Series {

    private int numberOfGames;
    private Team away;
    private Team home;

    public Series(int numberOfGames, Team away, Team home) {
        this.numberOfGames = numberOfGames;
        this.away = away;
        this.home = home;
    }

    public int getNumberOfGames() {
        return numberOfGames;
    }

    public void setNumberOfGames(int numberOfGames) {
        this.numberOfGames = numberOfGames;
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

    public List<Game> getGames(LocalDate startDate) {
        List<Game> games = new ArrayList<>();
        LocalDate date = startDate;
        for (int i = 0; i < numberOfGames; i++) {
            games.add(new Game(date, away, home));
            date = date.plusDays(1);

            away.setMostRecentGame(date);
            home.setMostRecentGame(date);
        }
        return games;
    }

    @Override
    public String toString() {
        return "Series{" +
            "numberOfGames=" + numberOfGames +
            ", away=" + away.getCode() +
            ", home=" + home.getCode() +
            '}';
    }
}
