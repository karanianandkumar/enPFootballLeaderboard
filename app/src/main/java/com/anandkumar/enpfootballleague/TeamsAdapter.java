package com.anandkumar.enpfootballleague;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Anand on 11/25/2016.
 */

public class TeamsAdapter extends RecyclerView.Adapter<TeamsAdapter.MyViewHolder> {

    private List<Team> teamList;

    public TeamsAdapter(List<Team> teamList) {
        this.teamList = teamList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView rank, played, won, lost, drawn, gd;

        public MyViewHolder(View view) {
            super(view);
            rank = (TextView) view.findViewById(R.id.rankTV);
            played = (TextView) view.findViewById(R.id.playedTV);
            won = (TextView) view.findViewById(R.id.wonTV);
            lost = (TextView) view.findViewById(R.id.lostTV);
            drawn = (TextView) view.findViewById(R.id.drawnTV);
            gd = (TextView) view.findViewById(R.id.gdTV);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teamlist_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Team team = teamList.get(position);
        holder.rank.setText("Rank" + (position + 1) + ":  Team " + team.getName() + "\n");
        holder.played.setText("Played: " + team.getPlayed());
        holder.won.setText("Won: " + team.getWon());
        holder.lost.setText("Lost: " + team.getLost());
        holder.drawn.setText("Drawn: " + team.getDrawn());
        holder.gd.setText("GD: " + (team.getGd() > 0 ? "+" : "") + +team.getGd());
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }


}
