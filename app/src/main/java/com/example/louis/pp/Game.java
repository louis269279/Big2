package com.example.louis.pp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by louis on 27/06/2017.
 */
public class Game {

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

    MainActivity ma;

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

//    public int describeContents() {
//        return 0;
//    }
//
//    /** save object in parcel */
//    public void writeToParcel(Parcel out, int flags) {
//        out.writeArray(hands);
//        out.writeIntArray(playerPoints);
//        out.writeInt(whoseTurn);
//        out.writeList(prevPlay);
//        out.writeInt(numPasses);
//        out.writeInt(currStatus);
//        out.writeInt(prevStatus);
//        out.writeInt(prevDecider);
//        out.writeInt(currDecider);
//        out.writeByte((byte) (firstMove ? 1 : 0));
//        out.writeStringArray(playerNames);
//    }
//
//    public final Parcelable.Creator<Game> CREATOR
//            = new Parcelable.Creator<Game>() {
//        public Game createFromParcel(Parcel in) {
//            return new Game(in);
//        }
//
//        public Game[] newArray(int size) {
//            return new Game[size];
//        }
//    };
//
//    /** recreate object from parcel */
//    private Game(Parcel in, MainActivity ma) {
//        hands = (ArrayList<Integer>[]) in.readArray(hands.getClass().getClassLoader());
//        in.readIntArray(playerPoints);
//        whoseTurn = in.readInt();
//        prevPlay = in.readArrayList(prevPlay.getClass().getClassLoader());
//        numPasses = in.readInt();
//        currStatus = in.readInt();
//        prevStatus = in.readInt();
//        prevDecider = in.readInt();
//        currDecider = in.readInt();
//        firstMove = in.readByte() != 0;
//        in.readStringArray(playerNames);
//    }

    public Game(String n1, String n2, String n3, String n4, MainActivity ma, boolean deal) {
        this.ma = ma;
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
        if (deal) dealCards();
    }

    public void newGame() {
        for (int i = 0; i < NUM_PLAYERS; i++) hands[i].clear();
        prevPlay.clear();
        numPasses = 0;
        dealCards();
    }

    public boolean isLegalMove(ArrayList<Integer> cs) {

        // must own all cards
        if (hands[whoseTurn].containsAll(cs) == false) {
            new AlertDialog.Builder(ma)
                    .setTitle("Error!")
                    .setMessage("You don't own this card!")
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
            new AlertDialog.Builder(ma)
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
            new AlertDialog.Builder(ma)
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
            new AlertDialog.Builder(ma)
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
            new AlertDialog.Builder(ma)
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

    public boolean isBigger(ArrayList<Integer> cs) {

        if (lastMove() == null) return true;
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

    // compare if value of a > b
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

    public boolean isGameOver() {
        if (hands[PLAYER_ONE].isEmpty() || hands[PLAYER_TWO].isEmpty() ||
                hands[PLAYER_THREE].isEmpty() || hands[PLAYER_FOUR].isEmpty()) {
            return true;
        }
        return false;
    }

    public void dealCards() {
        boolean[] dealt = new boolean[SIZE_OF_DECK];
        Random r = new Random();
        int cardsDealt = 0;
        while (cardsDealt != SIZE_OF_DECK) {
            int cardNum = r.nextInt(SIZE_OF_DECK);
            if (dealt[cardNum] == false) {
                if (firstMove && cardNum == THREE_OF_DIAMONDS && ma.playerTypes[cardsDealt % NUM_PLAYERS] == 2) continue;
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

    public void addPoints() {
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

    public ArrayList<Integer> lastMove() {
        for (int i = 0; i < prevPlay.size(); i++) {
            if (prevPlay.get(i).isEmpty() == false) return prevPlay.get(i);
        }
        return null;
    }

    public Game cloneState() {
        Game newGame = new Game(playerNames[0], playerNames[1], playerNames[2], playerNames[3], ma, false);
        for (int i = 0; i < NUM_PLAYERS; i++) {
            newGame.hands[i].addAll(this.hands[i]);
        }
        newGame.playerPoints = this.playerPoints;
        newGame.whoseTurn = this.whoseTurn;
        newGame.numPasses = this.numPasses;
        for (int i = 0; i < prevPlay.size(); i++) {
            newGame.prevPlay.add(this.prevPlay.get(i));
        }
        newGame.numPasses = this.numPasses;
        newGame.currStatus = this.currStatus;
        newGame.prevStatus = this.prevStatus;
        newGame.prevDecider = this.prevDecider;
        newGame.currDecider = this.currDecider;
        newGame.firstMove = this.firstMove;

        return newGame;
    }

    public void makeMove(ArrayList<Integer> move) {
        if (move.size() == 0) {
            // pass
            if (numPasses == NUM_PLAYERS-1 || lastMove() == null) {

            } else {
                numPasses++;
                prevPlay.add(0, new ArrayList<Integer>());
                if (numPasses == NUM_PLAYERS - 1) prevPlay.clear();
                whoseTurn = (whoseTurn + 1) % NUM_PLAYERS;
            }
        } else {
            if (isLegalMove(move)) {
                firstMove = false;
                numPasses = 0;
                hands[whoseTurn].removeAll(move);
                prevPlay.add(0, move);
                if (prevPlay.size() == NUM_PLAYERS) prevPlay.remove(NUM_PLAYERS-1);
                prevStatus = currStatus;
                prevDecider = currDecider;


                if (isGameOver() == false) {
                    whoseTurn = (whoseTurn+1) % NUM_PLAYERS;
                } else {
                    addPoints();
                }
            }
        }
    }
}