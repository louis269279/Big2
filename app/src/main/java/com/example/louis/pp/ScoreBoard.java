package com.example.louis.pp;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ScoreBoard extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_board);

        Bundle bundle = getIntent().getExtras();
        String[] playerNames = bundle.getStringArray("playerNames");
        int[] playerScores = bundle.getIntArray("scores");

        ((TextView) findViewById(R.id.p0Name)).setText(playerNames[0]);
        ((TextView) findViewById(R.id.p1Name)).setText(playerNames[1]);
        ((TextView) findViewById(R.id.p2Name)).setText(playerNames[2]);
        ((TextView) findViewById(R.id.p3Name)).setText(playerNames[3]);

        ((ProgressBar) findViewById(R.id.scoreBar0)).setProgress(playerScores[0]);
        ((ProgressBar) findViewById(R.id.scoreBar1)).setProgress(playerScores[1]);
        ((ProgressBar) findViewById(R.id.scoreBar2)).setProgress(playerScores[2]);
        ((ProgressBar) findViewById(R.id.scoreBar3)).setProgress(playerScores[3]);

        System.out.println(Build.VERSION.SDK_INT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        ((ProgressBar) findViewById(R.id.scoreBar0)).setProgressBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        ((ProgressBar) findViewById(R.id.scoreBar1)).setProgressBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        ((ProgressBar) findViewById(R.id.scoreBar2)).setProgressBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        ((ProgressBar) findViewById(R.id.scoreBar3)).setProgressBackgroundTintList(ColorStateList.valueOf(Color.WHITE));

        ((ProgressBar) findViewById(R.id.scoreBar0)).setProgressTintList(ColorStateList.valueOf(Color.BLACK));
        ((ProgressBar) findViewById(R.id.scoreBar1)).setProgressTintList(ColorStateList.valueOf(Color.BLACK));
        ((ProgressBar) findViewById(R.id.scoreBar2)).setProgressTintList(ColorStateList.valueOf(Color.BLACK));
        ((ProgressBar) findViewById(R.id.scoreBar3)).setProgressTintList(ColorStateList.valueOf(Color.BLACK));

        ((TextView) findViewById(R.id.score0)).setText(Integer.toString(playerScores[0]));
        ((TextView) findViewById(R.id.score1)).setText(Integer.toString(playerScores[1]));
        ((TextView) findViewById(R.id.score2)).setText(Integer.toString(playerScores[2]));
        ((TextView) findViewById(R.id.score3)).setText(Integer.toString(playerScores[3]));
    }

    public void returnToGame(View view) {
        finish();
    }
}
