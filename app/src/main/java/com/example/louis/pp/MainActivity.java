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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable("Game", g);
        super.onSaveInstanceState(savedInstanceState);
    }

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
                bundle.getString("name3"), bundle.getString("name4"));
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
        while (playerTypes[g.whoseTurn] == 1) {
            g.numPasses++;
            g.prevPlay.add(0, new ArrayList<Integer>());
            if (g.numPasses == g.NUM_PLAYERS - 1) g.prevPlay.clear();
            g.whoseTurn = (g.whoseTurn + 1) % g.NUM_PLAYERS;
        }
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

    private void makeMoveEasy() {
        if (g.lastMove().size() == 1 || g.lastMove().size() == 2 || g.lastMove().size() == 3) {
            int freq[] = new int[13];
            for (int i: g.hands[g.whoseTurn]) {
                freq[i % 13]++;
            }
            for (int i: freq) {
                if (i == g.hands[g.whoseTurn].size()) {
                    //g.makeMove()
                }
            }
        } else if (g.lastMove().size() == 5) {

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


    public class Game implements Parcelable {

        ArrayList<Integer>[] hands;
        int[] playerPoints;
        int whoseTurn;
        ArrayList<ArrayList<Integer>> prevPlay;
        int numPasses;
        int currStatus;
        int prevStatus;
        int prevDecider;
        int currDecider;
        boolean firstMove;
        String playerNames[];

        final static int PLAYER_ONE = 0;
        final static int PLAYER_TWO = 1;
        final static int PLAYER_THREE = 2;
        final static int PLAYER_FOUR = 3;
        final static int NUM_PLAYERS = 4;

        final static int STRAIGHT = 1;
        final static int FLUSH = 2;
        final static int FULL_HOUSE = 3;
        final static int QUAD = 4;
        final static int STRAIGHT_FLUSH = 5;

        final static int THREE_OF_DIAMONDS = 2;
        final static int SIZE_OF_DECK = 52;
        final static int TWO = 1;
        final static int ACE = 0;

        final static int SORT_VALUE = 1;
        final static int SORT_SUIT = 2;

        public int describeContents() {
            return 0;
        }

        /** save object in parcel */
        public void writeToParcel(Parcel out, int flags) {
            out.writeArray(hands);
            out.writeIntArray(playerPoints);
            out.writeInt(whoseTurn);
            out.writeList(prevPlay);
            out.writeInt(numPasses);
            out.writeInt(currStatus);
            out.writeInt(prevStatus);
            out.writeInt(prevDecider);
            out.writeInt(currDecider);
            out.writeByte((byte) (firstMove ? 1 : 0));
            out.writeStringArray(playerNames);
        }

        public final Parcelable.Creator<Game> CREATOR
                = new Parcelable.Creator<Game>() {
            public Game createFromParcel(Parcel in) {
                return new Game(in);
            }

            public Game[] newArray(int size) {
                return new Game[size];
            }
        };

        /** recreate object from parcel */
        private Game(Parcel in) {
            hands = (ArrayList<Integer>[]) in.readArray(hands.getClass().getClassLoader());
            in.readIntArray(playerPoints);
            whoseTurn = in.readInt();
            prevPlay = in.readArrayList(prevPlay.getClass().getClassLoader());
            numPasses = in.readInt();
            currStatus = in.readInt();
            prevStatus = in.readInt();
            prevDecider = in.readInt();
            currDecider = in.readInt();
            firstMove = in.readByte() != 0;
            in.readStringArray(playerNames);
        }

        public Game(String n1, String n2, String n3, String n4) {
            hands = (ArrayList<Integer>[]) new ArrayList[NUM_PLAYERS];
            prevPlay = new ArrayList<ArrayList<Integer>>();
            playerPoints = new int[NUM_PLAYERS];
            firstMove = true;
            numPasses = 0;
            playerNames = new String[4];
            playerNames[0] = n1;
            playerNames[1] = n2;
            playerNames[2] = n3;
            playerNames[3] = n4;
            for (int i = 0; i < NUM_PLAYERS; i++) {
                hands[i] = new ArrayList<Integer>();
            }
            dealCards();
        }

        private void newGame() {
            for (int i = 0; i < NUM_PLAYERS; i++) hands[i].clear();
            prevPlay.clear();
            numPasses = 0;
            dealCards();
        }

        private boolean isLegalMove(ArrayList<Integer> cs) {

            // must own all cards
            if (hands[whoseTurn].containsAll(cs) == false) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Error!")
                        .setMessage("Can't Pass, it's a free go!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                System.out.println("You don't own this card!");
                return false;
            }

            // must be same size as prev play.
            if (lastMove() != null && lastMove().size() != cs.size()) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Error!")
                        .setMessage("Not same type as last combination!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return false;
            }

            // check if valid combination
            if (isValidCombo(cs) == false) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Error!")
                        .setMessage("Not a valid combination!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return false;
            }

            // check if bigger than previous play.
            if (lastMove() != null && isBigger(cs) == false) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Error!")
                        .setMessage("Not big enough!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return false;
            }

            // if first move, must use diamond 3
            if (firstMove && cs.contains(THREE_OF_DIAMONDS) == false) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Error!")
                        .setMessage("Must use three of diamonds!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return false;
            }

            return true;
        }

        private boolean isBigger(ArrayList<Integer> cs) {

            if (cs.size() >= 1 && cs.size() <= 3) {

                if (cs.size() == 2 && cs.get(0) % 13 == lastMove().get(0) % 13) { // if same double
                    return (cs.contains(39 + (cs.get(0) % 13)));
                }

                if (isBigger(cs.get(0), lastMove().get(0))) return true;
            } else { // must be size 5, otherwise not valid
                //assert(cs.size() == 5);
                //assert(currStatus != 0);
                //assert(prevStatus != 0);

                // straight flush > quad > trip > flush > straight
                if (currStatus > prevStatus) return true;
                if (currStatus == prevStatus && isBigger(currDecider, prevDecider)) return true;
            }
            return false;
        }

        private boolean isBigger(int a, int b) {
            if (a % 13 == b % 13 && a/13 > b/13) return true; // same card, diff suit

            if (b % 13 == TWO) return false; // if prev is 2.
            else if (a % 13 == TWO) return true;
            else if (b % 13 == ACE) return false; // if prev is A
            else if (a % 13 == ACE) return true;
            else if (a % 13 > b % 13) return true;
            return false;
        }

        private boolean isValidCombo(ArrayList<Integer> cs) {

            if (cs.size() == 1) {
                return true;
            } else if (cs.size() == 2) { // must be double
                if (cs.get(0) % 13 == cs.get(1) % 13) return true;
            } else if (cs.size() == 3) { // triple
                if (cs.get(0) % 13 == cs.get(1) % 13 && cs.get(1) % 13 == cs.get(2) % 13) return true;
            } else if (cs.size() != 5) {
                return false;
            }

            int freq[] = new int[13];
            int suit[] = new int[4];
            for (int c: cs) {
                freq[c % 13]++;
                suit[c/13]++;
            }

            boolean trip = false, doub = false;
            currStatus = 0;
            int consec = 0;
            for (int i = 0; i < 13; i++) {
                if (freq[i] != 1) consec = 0;
                else {
                    consec++;
                    if (consec == 5) {
                        currStatus = STRAIGHT;
                        currDecider = find(cs, i);
                        break;
                    }
                }
                if (freq[i] == 2) doub = true;
                else if (freq[i] == 3) {
                    trip = true;
                    currDecider = i;
                }
                else if (freq[i] == 4) {
                    currStatus = QUAD;
                    currDecider = i;
                    break;
                }
            }

            if (trip && doub) currStatus = FULL_HOUSE;
            if (consec == 4 && freq[0] == 1) {
                currStatus = STRAIGHT; // 10 J Q K A combo
                currDecider = find(cs, ACE);
            }
            if (suit[0] == 5 || suit[1] == 5 || suit[2] == 5 || suit[3] == 5) {
                if (currStatus == STRAIGHT) currStatus = STRAIGHT_FLUSH;
                else {
                    currStatus = FLUSH;
                    if (find(cs, TWO) != -1) currDecider = find(cs, TWO);
                    else if (find(cs, ACE) != -1) currDecider = find(cs, ACE);
                    else currDecider = Collections.max(cs);
                }
            }

            if (currStatus != 0) return true;
            return false;
        }

        private int find(ArrayList<Integer> cs, int i) {
            for (int j: cs) {
                if (j % 13 == i) return j;
            }
            return -1;
        }

        private boolean isGameOver() {
            if (hands[PLAYER_ONE].isEmpty() || hands[PLAYER_TWO].isEmpty() ||
                    hands[PLAYER_THREE].isEmpty() || hands[PLAYER_FOUR].isEmpty()) {
                return true;
            }
            return false;
        }

        private void dealCards() {
            boolean[] dealt = new boolean[SIZE_OF_DECK];
            Random r = new Random();
            int cardsDealt = 0;
            while (cardsDealt != SIZE_OF_DECK) {
                int cardNum = r.nextInt(SIZE_OF_DECK);
                if (dealt[cardNum] == false) {
                    if (firstMove && cardNum == THREE_OF_DIAMONDS && playerTypes[cardsDealt % NUM_PLAYERS] == 1) continue;
                    hands[cardsDealt % NUM_PLAYERS].add(cardNum);
                    if (firstMove && cardNum == THREE_OF_DIAMONDS) whoseTurn = cardsDealt % NUM_PLAYERS;
                    cardsDealt++;
                    dealt[cardNum] = true;
                }
            }
            for (int i = 0; i < NUM_PLAYERS; i++) sortHand(SORT_VALUE, i);
        }

        void sortHand(int sortType, int hand) {

            if (sortType == SORT_VALUE) {
                ArrayList<Integer> sorted = new ArrayList<Integer>();
                while (hands[hand].size() != 0) {
                    int max = hands[hand].get(0);
                    int pos = 0;
                    for (int i = 0; i < hands[hand].size(); i++) {
                        if (isBigger(hands[hand].get(i), max)) {
                            pos = i;
                            max = hands[hand].get(i);
                        }
                    }
                    sorted.add(0, max);
                    hands[hand].remove(pos);
                }
                hands[hand] = sorted;
            } else if (sortType == SORT_SUIT) {
                Collections.sort(hands[hand]);
            }
        }

        private void addPoints() {
            for (int i = 0; i < NUM_PLAYERS; i++) {
                if (hands[i].size() <= 7) {
                    playerPoints[i] += hands[i].size();
                } else if (hands[i].size() <= 9) {
                    playerPoints[i] += 2*hands[i].size();
                } else if (hands[i].size() <= 12) {
                    playerPoints[i] += 3*hands[i].size();
                } else if (hands[i].size() == 13) {
                    playerPoints[i] += 4*hands[i].size();
                }
            }
        }

        private ArrayList<Integer> lastMove() {
            for (int i = 0; i < prevPlay.size(); i++) {
                if (prevPlay.get(i).isEmpty() == false) return prevPlay.get(i);
            }
            return null;
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
