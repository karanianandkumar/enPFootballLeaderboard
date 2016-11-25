package com.anandkumar.enpfootballleague;

/**
 * Created by Anand on 11/25/2016.
 */

public class Team implements Comparable<Team> {

    private int won, played, lost, drawn, gd, points;
    private String name;

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWon() {
        return won;
    }

    public void setWon(int won) {
        this.won = won;
    }

    public int getPlayed() {
        return played;
    }

    public void setPlayed(int played) {
        this.played = played;
    }

    public int getLost() {
        return lost;
    }

    public void setLost(int lost) {
        this.lost = lost;
    }

    public int getDrawn() {
        return drawn;
    }

    public void setDrawn(int drawn) {
        this.drawn = drawn;
    }

    public int getGd() {
        return gd;
    }

    public void setGd(int gd) {
        this.gd = gd;
    }

    /* For Updating leauge table based on Win-Loss-Draw and GD*/
    @Override
    public int compareTo(Team team) {

        if (this.getPoints() > team.getPoints()) {
            return -1;
        } else if (this.getPoints() == team.getPoints()) {
             if (this.getGd() > team.getGd()) {
                    return -1;
                } else if (this.getGd() == team.getGd()) {
                    return this.getName().compareTo(team.getName());
                }
            }


        return 1;
    }
}
