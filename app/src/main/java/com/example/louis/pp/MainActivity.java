package com.example.louis.pp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {

    Game g;
    ImageButton[] ibs;
    ImageView[][] ivs;
    boolean[] selected;
    int[] playerTypes;

    final static int EASY = 1;
    final static int MED = 2;
    final static int HARD = 3;
    final static int NONE = 4;

    /*@Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable("Game", g);
        super.onSaveInstanceState(savedInstanceState);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = getIntent().getExtras();
        playerTypes = new int[g.NUM_PLAYERS];
        playerTypes[0] = bundle.getInt("type1");
        playerTypes[1] = bundle.getInt("type2");
        playerTypes[2] = bundle.getInt("type3");
        playerTypes[3] = bundle.getInt("type4");

        g = new Game(bundle.getString("name1"), bundle.getString("name2"),
                bundle.getString("name3"), bundle.getString("name4"), this, true);

        selected = new boolean[13];
        ibs = new ImageButton[g.SIZE_OF_DECK / g.NUM_PLAYERS];
        ivs = new ImageView[g.NUM_PLAYERS-1][g.SIZE_OF_DECK / g.NUM_PLAYERS];
        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTurns();
            }
        };
        findViewById(R.id.lm0).setOnClickListener(ocl);
        findViewById(R.id.lm1).setOnClickListener(ocl);
        findViewById(R.id.lm2).setOnClickListener(ocl);
        findViewById(R.id.lm3).setOnClickListener(ocl);
        findViewById(R.id.lm4).setOnClickListener(ocl);

        changeTurns();
    }

    public void changeTurns() {

        /*while (playerTypes[g.whoseTurn] != 0) {
            // ai make move.
            if (playerTypes[g.whoseTurn] == EASY) {

            } else if (playerTypes[g.whoseTurn] == MED) {

            } else if (playerTypes[g.whoseTurn] == HARD) {

            }
        }*/
        while (playerTypes[g.whoseTurn] != 0) {

            if (playerTypes[g.whoseTurn] == 2) {
                g.numPasses++;
                g.prevPlay.add(0, new ArrayList<Integer>());
                if (g.numPasses == g.NUM_PLAYERS - 1) g.prevPlay.clear();
                g.whoseTurn = (g.whoseTurn + 1) % g.NUM_PLAYERS;
            } else {
                AI ai = new AI(g.whoseTurn);
                ArrayList<Integer> move = new ArrayList<Integer>(ai.nextMove(g));
                if (move.size() == 0) {
                    g.numPasses++;
                    g.prevPlay.add(0, new ArrayList<Integer>());
                    if (g.numPasses == g.NUM_PLAYERS - 1) g.prevPlay.clear();
                    g.whoseTurn = (g.whoseTurn + 1) % g.NUM_PLAYERS;

                } else {
                    if (g.isLegalMove(move)) {
                        g.firstMove = false;
                        g.numPasses = 0;
                        g.hands[g.whoseTurn].removeAll(move);
                        g.prevPlay.add(0, move);
                        if (g.prevPlay.size() == g.NUM_PLAYERS)
                            g.prevPlay.remove(g.NUM_PLAYERS - 1);
                        g.prevStatus = g.currStatus;
                        g.prevDecider = g.currDecider;


                        if (g.isGameOver() == false) {
                            g.whoseTurn = (g.whoseTurn + 1) % g.NUM_PLAYERS;
                        } else {
                            new AlertDialog.Builder(this)
                                    .setTitle("Congratulations!")
                                    .setMessage(g.playerNames[g.whoseTurn] + " Won!")
                                    .setPositiveButton("Play again", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            g.newGame();
                                            changeTurns();
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                            g.addPoints();
                        }
                    }
                }
            }
        }

        if (g.isGameOver() == false) {
            Intent intent = new Intent(MainActivity.this, ChangeTurns.class);
            intent.putExtra("whoseTurn", g.whoseTurn);
            intent.putExtra("pastPlays", g.prevPlay);
            intent.putExtra("playerNames", g.playerNames);
            if (g.lastMove() == null) {
                intent.putExtra(("freeGo"), true);
            } else {
                intent.putExtra(("freeGo"), false);
            }
            startActivity(intent);
        }
    }

    public void displayScoreBoard(View view) {
        Intent intent = new Intent(MainActivity.this, ScoreBoard.class);
        intent.putExtra("playerNames", g.playerNames);
        intent.putExtra("scores", g.playerPoints);
        startActivity(intent);
    }

    public void playGame(View view) {
        if (view.getId() == R.id.passButton) {
            if (g.numPasses == g.NUM_PLAYERS-1 || g.lastMove() == null) {
                new AlertDialog.Builder(this)
                        .setTitle("Error!")
                        .setMessage("Can't Pass, it's a free go!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            } else {
                g.numPasses++;
                g.prevPlay.add(0, new ArrayList<Integer>());
                if (g.numPasses == g.NUM_PLAYERS - 1) g.prevPlay.clear();
                g.whoseTurn = (g.whoseTurn + 1) % g.NUM_PLAYERS;
                // change player turn activity.
                changeTurns();
            }
        } else if (view.getId() == R.id.playButton) {
            ArrayList<Integer> cs = new ArrayList<Integer>();
            for (int i = 0; i < g.hands[g.whoseTurn].size(); i++) {
                if (selected[i]) {
                    cs.add((Integer) ibs[i].getTag());
                }
            }
            if (g.isLegalMove(cs)) {
                g.firstMove = false;
                g.numPasses = 0;
                g.hands[g.whoseTurn].removeAll(cs);
                g.prevPlay.add(0, cs);
                if (g.prevPlay.size() == g.NUM_PLAYERS) g.prevPlay.remove(g.NUM_PLAYERS-1);
                g.prevStatus = g.currStatus;
                g.prevDecider = g.currDecider;


                if (g.isGameOver() == false) {
                    g.whoseTurn = (g.whoseTurn+1) % g.NUM_PLAYERS;
                    // change player turn activity.
                    changeTurns();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Congratulations!")
                            .setMessage(g.playerNames[g.whoseTurn] + " Won!")
                            .setPositiveButton("Play again", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    g.newGame();
                                    changeTurns();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    g.addPoints();
                }
            }
        } else if (view.getId() == R.id.sort1) {
            g.sortHand(g.SORT_VALUE, g.whoseTurn);
            displayGame();
        } else if (view.getId() == R.id.sort2) {
            g.sortHand(g.SORT_SUIT, g.whoseTurn);
            displayGame();
        }
    }

    public void displayGame() {

        View.OnClickListener select = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = -1;
                switch (v.getId()) {
                    case R.id.c0:
                        i = 0;
                        break;
                    case R.id.c1:
                        i = 1;
                        break;
                    case R.id.c2:
                        i = 2;
                        break;
                    case R.id.c3:
                        i = 3;
                        break;
                    case R.id.c4:
                        i = 4;
                        break;
                    case R.id.c5:
                        i = 5;
                        break;
                    case R.id.c6:
                        i = 6;
                        break;
                    case R.id.c7:
                        i = 7;
                        break;
                    case R.id.c8:
                        i = 8;
                        break;
                    case R.id.c9:
                        i = 9;
                        break;
                    case R.id.c10:
                        i = 10;
                        break;
                    case R.id.c11:
                        i = 11;
                        break;
                    case R.id.c12:
                        i = 12;
                        break;
                }
                selected[i] = !selected[i];
                if (selected[i]) { // #ffd582
                    ((ImageButton)v).setColorFilter(Color.argb(255, 255, 213, 130),
                            PorterDuff.Mode.MULTIPLY);
                } else {
                    ((ImageButton)v).setColorFilter(null);
                }
            }
        };

        for (int i = 0; i < g.NUM_PLAYERS; i++) {
            for (int j = 0; j < g.SIZE_OF_DECK/g.NUM_PLAYERS; j++) {
                switch(i) {
                    case 0:
                        ibs[j] = (ImageButton) findViewById(getResources().getIdentifier("c"+j,
                                "id", getPackageName()));
                        if (i < g.hands[g.whoseTurn].size()) ibs[j].setOnClickListener(select);
                        selected[j] = false;
                        ibs[j].setColorFilter(null);
                        break;
                    case 1:
                        ivs[i-1][j] = (ImageView) findViewById(getResources().getIdentifier("rightv"+j,
                                "id", getPackageName()));
                        break;

                    case 2:
                        ivs[i-1][j] = (ImageView) findViewById(getResources().getIdentifier("topv"+j,
                                "id", getPackageName()));
                        break;

                    default:
                        ivs[i-1][j] = (ImageView) findViewById(getResources().getIdentifier("leftv"+j,
                                "id", getPackageName()));
                        break;
                }
            }
        }

        for (int i = 0; i < 13; i++) {
            if (i < g.hands[g.whoseTurn].size()) {
                ibs[i].setImageResource(getResources().getIdentifier("img" + g.hands[g.whoseTurn].get(i), "drawable", getPackageName()));
                ibs[i].setTag(g.hands[g.whoseTurn].get(i));
                ibs[i].setVisibility(ImageButton.VISIBLE);
            } else {
                ibs[i].setVisibility(ImageButton.INVISIBLE);
            }
        }

        for (int i = 1; i < g.NUM_PLAYERS; i++) {
            for (int j = 0; j < g.SIZE_OF_DECK/g.NUM_PLAYERS; j++) {
                if (j < g.hands[(g.whoseTurn+i)%4].size()) {
                    ivs[i-1][j].setImageResource(R.drawable.img54);
                    ivs[i-1][j].setVisibility(ImageButton.VISIBLE);
                } else {
                    ivs[i-1][j].setVisibility(ImageButton.INVISIBLE);
                }
            }
        }

        ((TextView) findViewById(R.id.p0Name)).setText(g.playerNames[g.whoseTurn]);
        ((TextView) findViewById(R.id.p1Name)).setText(g.playerNames[(g.whoseTurn+1)%4]);
        ((TextView) findViewById(R.id.p2Name)).setText(g.playerNames[(g.whoseTurn+2)%4]);
        ((TextView) findViewById(R.id.p3Name)).setText(g.playerNames[(g.whoseTurn+3)%4]);

        ArrayList<Integer> lm = g.lastMove();
        for (int i = 0; i < 5; i++) {
            if (lm != null && i < lm.size()) {
                ((ImageButton) findViewById(getResources().getIdentifier("lm" + i,
                        "id", getPackageName()))).setImageResource(getResources().getIdentifier("img" +
                        lm.get(i), "drawable", getPackageName()));
                findViewById(getResources().getIdentifier("lm" + i,
                        "id", getPackageName())).setVisibility(ImageButton.VISIBLE);
            } else {
                findViewById(getResources().getIdentifier("lm" + i,
                        "id", getPackageName())).setVisibility(ImageButton.INVISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        displayGame();
    }

    public void quitButton(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Quit")
                .setMessage("Are you sure? All progress will be lost.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
