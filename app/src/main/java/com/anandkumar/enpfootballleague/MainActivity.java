package com.anandkumar.enpfootballleague;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.helpshift.All;
import com.helpshift.Core;
import com.helpshift.exceptions.InstallException;
import com.helpshift.support.Support;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private List<Team> teamList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TeamsAdapter teamsAdapter;
    private String TAG = MainActivity.class.getSimpleName();
    private ImageView downloadButton;
    private LinearLayout downloadLayout;
    private TextView downloadTV;
    private ProgressDialog pDialog;
    HashMap config = new HashMap();

    private static String url = "https://xobin.com/static/xobin_playground/enparadigm/FootballScoresData.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Integrate In-App Messaging with HelpShift
        initializeHelp();

        downloadLayout = (LinearLayout) findViewById(R.id.downloadLayout);
        downloadButton = (ImageView) findViewById(R.id.downloadButton);
        downloadTV = (TextView) findViewById(R.id.downloadTV);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        teamsAdapter = new TeamsAdapter(teamList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(teamsAdapter);

        downloadTV.setOnClickListener(this);
        downloadButton.setOnClickListener(this);


    }

    public void initializeHelp() {
        Core.init(All.getInstance());
        try {
            Core.install(getApplication(),
                    "8711f75a8f6b40fe2c5449b5bcee1601",
                    "xobintest.helpshift.com",
                    "xobintest_platform_20161125153344404-683c90ec4fcc95d",
                    config);
        } catch (InstallException e) {
            Log.e(TAG, "invalid install credentials : ", e);
        }
    }

    @Override
    public void onClick(View view) {
        new GetTeamsData().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.messaging:
                Support.showConversation(MainActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class GetTeamsData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Downloading...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            //Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    Iterator<String> iter = jsonObj.keys(); //For team Names
                    while (iter.hasNext()) {
                        Team team = new Team();
                        String key = iter.next();
                        JSONObject matchData = (JSONObject) jsonObj.get(key);
                        team.setName(key);
                        int matchesCount = matchData.length();
                        team.setPlayed(matchesCount);

                        /* Reading all matches data of a Team and update the data*/
                        for (int i = 0; i < matchesCount; i++) {
                            String matchNo = "match_" + (i + 1);
                            String scores[] = matchData.getString(matchNo).split("-");

                            int s1 = Integer.parseInt(scores[0]);
                            int s2 = Integer.parseInt(scores[1]);
                            if (s1 > s2) {
                                team.setWon(team.getWon() + 1);
                                team.setPoints(team.getPoints() + 3);
                            } else if (s1 < s2) {
                                team.setLost(team.getLost() + 1);
                            } else {
                                team.setDrawn(team.getDrawn() + 1);
                                team.setPoints(team.getPoints() + 1);
                            }
                            team.setGd(team.getGd() + (s1 - s2));
                        }
                        teamList.add(team);

                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Collections.sort(teamList);
            // Dismiss the downloading dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            //Download Complete! dialog
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog);
            dialog.setCancelable(false);
            TextView text = (TextView) dialog.findViewById(R.id.textDialog);
            text.setText("Download Complete!");
            dialog.show();

            // Displaying dialog for 2000ms
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    updateRecyclerView();
                }
            }, 2000);

        }

        /* For updating leauge table after downloading*/
        public void updateRecyclerView() {
            downloadLayout.setVisibility(LinearLayout.GONE);
            teamsAdapter.notifyDataSetChanged();
        }

    }
}
