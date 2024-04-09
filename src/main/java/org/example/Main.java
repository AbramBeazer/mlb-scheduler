package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final int YEAR = 2023;
    private static final LocalDate OPENING_DAY = LocalDate.of(YEAR, Month.MARCH, 30).with(TemporalAdjusters.nextOrSame(
        DayOfWeek.THURSDAY));
    private static final LocalDate ALL_STAR_GAME = LocalDate.of(YEAR, Month.JULY, 1)
        .with(TemporalAdjusters.dayOfWeekInMonth(2, DayOfWeek.TUESDAY));
    private static final LocalDate ALL_STAR_BREAK_BEGINS = ALL_STAR_GAME.minusDays(1);
    private static final LocalDate LAST_DAY_OF_ALL_STAR_BREAK = ALL_STAR_GAME.plusDays(2);

    public static void main(String[] args) {
        try (InputStream stream = Main.class.getClassLoader().getResourceAsStream("mlb.json")) {
            Objects.requireNonNull(stream, "Input stream cannot be null");

            Classification mlb = MAPPER.readValue(stream.readAllBytes(), Classification.class);

            List<Team> teams = mlb.getLeagues()
                .stream()
                .flatMap(l -> l.getDivisions().stream().flatMap(d -> d.getTeams().stream()))
                .collect(Collectors.toList());

            List<Series> series = getInterleagueSeries(mlb);
            series.addAll(getLeagueSeries(mlb));

            for (Team team : teams) {
                long awayCount = series.stream()
                    .filter(s -> s.getAway().equals(team)).count();

                long homeCount = series.stream()
                    .filter(s -> s.getHome().equals(team)).count();

                if (awayCount != 26 || homeCount != 26) {
                    throw new RuntimeException(
                        "Wrong schedule for " + team.getCode() + " -- away: " + awayCount + ", home: " + homeCount);
                }
            }

            List<Game> schedule = scheduleGames(series, teams);
            System.out.println(schedule);
        } catch (IOException | LeagueInputException e) {
            LOG.error(e.toString());
        }
    }

    private static List<Game> scheduleGames(List<Series> allSeries, List<Team> teams) {
        Collections.shuffle(allSeries);
        List<Game> games = new ArrayList<>();

        LocalDate today = OPENING_DAY;
        while (!allSeries.isEmpty()) {

            Set<Team> usedTeams = new HashSet<>();

            while (usedTeams.size() != teams.size()) {

                Series series = allSeries.stream()
                    .filter(s -> !usedTeams.contains(s.getAway()) && !usedTeams.contains(s.getHome()))
                    .findAny().orElse(null);

                if (series != null) {
                    if (today.equals(OPENING_DAY) && series.getNumberOfGames() == 2) {
                        continue;
                    }
                    usedTeams.add(series.getAway());
                    usedTeams.add(series.getHome());
                    games.addAll(series.getGames(today));
                }
            }
            today = today.plusDays(4);
        }
        return games;
    }

    private static List<Series> getLeagueSeries(Classification mlb) {
        List<Series> series = new ArrayList<>();

        for (League league : mlb.getLeagues()) {
            List<Division> divisions = league.getDivisions();
            for (int a = 0; a < divisions.size(); a++) {
                Division div = divisions.get(a);
                series.addAll(getDivisionSeries(div));

                for (int b = 0; b < divisions.size(); b++) {

                    if (a != b) {
                        List<Team> otherTeams = divisions.get(b).getTeams();

                        for (int i = 0; i < div.getTeams().size(); i++) {
                            Team team = div.getTeams().get(i);

                            for (int j = 0; j < otherTeams.size(); j++) {
                                Team otherTeam = otherTeams.get(j);

                                if ((j == i || j == (i + 1) % otherTeams.size())
                                    && b == (a + 1) % divisions.size()) {
                                    series.add(new Series(4, team, otherTeam));
                                } else {
                                    series.add(new Series(3, team, otherTeam));
                                }
                            }
                        }
                    }
                }
            }
        }

        if (series.size() != 540) {
            throw new RuntimeException("Failed to create 540 intraleague series; only created " + series.size());
        }
        return series;
    }

    private static List<Series> getDivisionSeries(Division division) {
        List<Series> series = new ArrayList<>();
        List<Team> teams = division.getTeams();

        for (int i = 0; i < teams.size(); i++) {
            for (int j = 0; j < teams.size(); j++) {
                if (i != j) {
                    Team away = teams.get(i);
                    Team home = teams.get(j);

                    series.add(new Series(3, away, home));
                    if ((i + 2) % teams.size() == j || (i + 1) % teams.size() == j) {
                        series.add(new Series(3, away, home));
                    } else {
                        series.add(new Series(4, away, home));
                    }
                }
            }
        }

        if (series.size() != 40) {
            throw new RuntimeException(
                "Failed to create 40 divisional series for " + division.getName() + "; only created "
                    + series.size());
        }

        return series;
    }

    private static List<Series> getInterleagueSeries(Classification mlb) throws LeagueInputException {
        List<Series> series = new ArrayList<>();

        List<Team> teams = mlb.getLeagues()
            .get(0)
            .getDivisions()
            .stream()
            .flatMap(d -> d.getTeams().stream())
            .collect(Collectors.toList());

        List<Team> otherTeams = mlb.getLeagues()
            .get(1)
            .getDivisions()
            .stream()
            .flatMap(d -> d.getTeams().stream())
            .collect(Collectors.toList());

        for (Team team : teams) {
            Team rival = otherTeams.stream()
                .filter(t -> team.getRival().equals(t.getCode()))
                .findFirst()
                .orElseThrow(() -> new LeagueInputException(
                    "Team " + team.getCode() + " has no rival designated in other league."));

            int rivalIndex = otherTeams.indexOf(rival);
            series.add(new Series(2, team, rival));
            series.add(new Series(2, rival, team));

            for (int j = 1; j <= 7; j++) {
                series.add(new Series(3, team, otherTeams.get((rivalIndex + j) % otherTeams.size())));
                series.add(new Series(3, otherTeams.get(Math.abs((rivalIndex + j + 7) % otherTeams.size())), team));
            }
        }
        return series;
    }
}