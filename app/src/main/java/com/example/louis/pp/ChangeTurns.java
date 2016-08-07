package com.example.louis.pp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class ChangeTurns extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_turns);
        Bundle bundle = getIntent().getExtras();
        int whoseTurn = bundle.getInt("whoseTurn");
        ArrayList<ArrayList<Integer>> pastPlays = (ArrayList<ArrayList<Integer>>) getIntent().getSerializableExtra("pastPlays");
        String[] playerNames = bundle.getStringArray("playerNames");
        ((TextView) findViewById(R.id.p2Text)).setText(playerNames[(whoseTurn+1)%4]);
        ((TextView) findViewById(R.id.p1Text)).setText(playerNames[(whoseTurn+2)%4]);
        ((TextView) findViewById(R.id.p0Text)).setText(playerNames[(whoseTurn+3)%4]);

        ImageButton[][] ibs = new ImageButton[3][5];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                ibs[i][j] = (ImageButton) findViewById(getResources().getIdentifier("p" + i+j,
                        "id", getPackageName()));
            }
        }

        if (bundle.getBoolean("freeGo")) {
            ((TextView) findViewById(R.id.freeGoText)).setVisibility(TextView.VISIBLE);
            for (int i = 0; i < 3; i++) for (int j = 0; j < 5; j++)
                ibs[i][j].setVisibility(ImageButton.INVISIBLE);
            ((TextView) findViewById(R.id.p2Text)).setVisibility(TextView.INVISIBLE);
            ((TextView) findViewById(R.id.p1Text)).setVisibility(TextView.INVISIBLE);
            ((TextView) findViewById(R.id.p0Text)).setVisibility(TextView.INVISIBLE);
            ((TextView) findViewById(R.id.p0Pass)).setVisibility(TextView.INVISIBLE);
            ((TextView) findViewById(R.id.p1Pass)).setVisibility(TextView.INVISIBLE);
            ((TextView) findViewById(R.id.p2Pass)).setVisibility(TextView.INVISIBLE);

        } else {
            ((TextView) findViewById(R.id.freeGoText)).setVisibility(TextView.INVISIBLE);
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 5; j++) {
                    if (i < pastPlays.size() && j < pastPlays.get(i).size()) {
                        ibs[i][j].setImageResource(getResources().getIdentifier("img" +
                                pastPlays.get(i).get(j), "drawable", getPackageName()));
                        ibs[i][j].setVisibility(ImageButton.VISIBLE);
                    } else {
                        ibs[i][j].setVisibility(ImageButton.INVISIBLE);
                    }
                }

                if (i < pastPlays.size() && pastPlays.get(i).size() == 0) {
                    ((TextView) findViewById(getResources().getIdentifier("p" + i + "Pass",
                            "id", getPackageName()))).setVisibility(TextView.VISIBLE);
                } else {
                    ((TextView) findViewById(getResources().getIdentifier("p" + i + "Pass",
                            "id", getPackageName()))).setVisibility(TextView.INVISIBLE);
                }
            }
        }

        ((TextView) findViewById(R.id.whoseTurnText)).setText(playerNames[whoseTurn] + "'s Turn");

    }

    public void goBack(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {

    }
}
