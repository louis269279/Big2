package com.example.louis.pp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by louis on 27/06/2017.
 */
public class AI {

    private int playerID;

    public AI (int playerID) {
        this.playerID = playerID;
    }

    public List<Integer> nextMove(Game g) {
        ArrayList<Integer> result = minimax(2, true, g);
        return result.subList(1, result.size());
    }

    private ArrayList<Integer> minimax(int depth, boolean maximizingPlayer, Game g) {

        ArrayList<ArrayList<Integer>> nextMoves = generateMoves(g);

        int bestScore = (maximizingPlayer) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int currentScore;
        ArrayList<Integer> bestMove = new ArrayList<Integer>();

        if (depth == 0 || nextMoves.size() == 0) {
            bestScore = evaluate(g);
        } else {
            for (ArrayList<Integer> move : nextMoves) {
                // try move with new game state
                Game gameClone = g.cloneState();
                gameClone.makeMove(move);

                if (maximizingPlayer) {
                    currentScore = minimax(depth - 1, false, gameClone).get(0);
                    if (currentScore > bestScore) {
                        bestScore = currentScore;
                        bestMove = move;
                    }
                } else {
                    currentScore = minimax(depth - 1, true, gameClone).get(0);
                    if (currentScore < bestScore) {
                        bestScore = currentScore;
                        bestMove = move;
                    }
                }
            }
        }
        bestMove.add(0, bestScore);
        return bestMove;
    }

    private ArrayList<ArrayList<Integer>> generateMoves(Game g) {

        ArrayList<ArrayList<Integer>> nextMoves = new ArrayList<ArrayList<Integer>>();

        if (g.isGameOver()) {
            return nextMoves;
        }

        ArrayList<ArrayList<Integer>> num = new ArrayList<ArrayList<Integer>>(13);
        for (int i = 0; i < 13; i++) num.add(new ArrayList<Integer>());
        for (int card: g.hands[g.whoseTurn]) {
            num.get(card % 13).add(card);
        }

        ArrayList<Integer> lastMove = g.lastMove();
        if (lastMove == null || lastMove.size() != 5) {
            // single, double or trip
            if (lastMove == null) {
                for (int i = 0; i < 13; i++) {
                    getCombinations(num.get(i).toArray(new Integer[num.get(i).size()]), num.get(i).size(), 1, nextMoves, g);
                    getCombinations(num.get(i).toArray(new Integer[num.get(i).size()]), num.get(i).size(), 2, nextMoves, g);
                    getCombinations(num.get(i).toArray(new Integer[num.get(i).size()]), num.get(i).size(), 3, nextMoves, g);
                }
            } else {
                for (int i = 0; i < 13; i++) {
                    if (num.get(i).size() >= lastMove.size()) {
                        getCombinations(num.get(i).toArray(new Integer[num.get(i).size()]), num.get(i).size(), lastMove.size(), nextMoves, g);
                    }
                }
            }
        }

        if (lastMove == null || lastMove.size() == 5) {
            // combo:
            ArrayList<ArrayList<Integer>> suit = new ArrayList<ArrayList<Integer>>(13);
            for (int i = 0; i < 13; i++) suit.add(new ArrayList<Integer>());
            for (int card: g.hands[g.whoseTurn]) {
                suit.get(card / 13).add(card);
            }

            // straight
            // find 5 consecutive numbers, if a value has more than one card, find
            // combination.
            int consec = 0;
            for (int i = 0; i < 13; i++) {
                if (num.get(i).size() == 0) consec = 0;
                else {
                    consec++;
                    if (consec == 5) {
                        // last five cards form a straight.
                        List<TempContainer> containers = new ArrayList<TempContainer>();
                        for (int j = 4; j >= 0; j--) {
                            containers.add(new TempContainer(num.get(i-j)));
                        }
                        List<ArrayList<Integer>> combinations = getCombination(0, containers);

                        for (ArrayList<Integer> move: combinations) {
                            if (g.isBigger(move)) {
                                nextMoves.add(move);
                            }
                        }
                        // continue, checking if next forms iteration
                        consec--;
                    }
                }
            }

            // flush
            // get combinations of all cards with same suit
            for (int i = 0; i < 4; i++) {
                if (suit.get(i).size() >= 5) {
                    getCombinations(suit.get(i).toArray(new Integer[suit.get(i).size()]), suit.get(i).size(), 5, nextMoves, g);
                }
            }

            // full house
            // find all triples and a double, form combination.
            pairCombinations(num, nextMoves, 2, 3, g);

            // quad
            // list of quads and singles.
            pairCombinations(num, nextMoves, 1, 4, g);

        }

        if (g.firstMove) {
            System.out.println("Before: " + Arrays.toString(nextMoves.toArray()));
            // remove all moves that don't contain diamond 3
            Iterator<ArrayList<Integer>> it = nextMoves.iterator();
            while (it.hasNext()) {
                if (it.next().contains(Game.THREE_OF_DIAMONDS) == false) {
                    it.remove();
                }
            }
            System.out.println("After: " + Arrays.toString(nextMoves.toArray()));
        }
        return nextMoves;
    }

    // highest score = best move, low score = bad move.
    private int evaluate(Game g) {
        int score = (g.whoseTurn == playerID) ? 1: -1;

        // game is over, best score.
        if (g.isGameOver()) {
            score *= 10000;
        }

        // calculate strength of current hand
        // less cards the better
        score += (13 - g.hands[g.whoseTurn].size()) * 1000;

        // stronger cards the better
        score += calcHandStrength(g.hands[g.whoseTurn]);
        return score;
    }

    private int calcHandStrength(ArrayList<Integer> hand) {
        int strength = 0;

        // single value strength
        for (int card: hand) {
            if (card % 13 == Game.TWO) strength += 200;
            else if (card % 13 == Game.ACE) strength += 100;
            else strength += ((card % 13) * 5);

        }

        //
        return strength;
    }

    private void pairCombinations(ArrayList<ArrayList<Integer>> num, ArrayList<ArrayList<Integer>> nextMoves, int first, int second, Game g) {
        ArrayList<ArrayList<Integer>> firstList = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> secondList = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> indexFirstList = new ArrayList<Integer>();
        ArrayList<Integer> indexSecondList = new ArrayList<Integer>();

        for (int i = 0; i < 13; i++) {
            if (num.get(i).size() >= first) {
                getCombinations(num.get(i).toArray(new Integer[num.get(i).size()]), num.get(i).size(), first, firstList, g);
            }

            if (num.get(i).size() >= second) {
                getCombinations(num.get(i).toArray(new Integer[num.get(i).size()]), num.get(i).size(), second, secondList, g);
            }
        }

        for (int i = 0; i < firstList.size(); i++) {
            indexFirstList.add(i);
        }
        for (int i = 0; i < secondList.size(); i++) {
            indexSecondList.add(indexSecondList.size() + i);
        }

        List<TempContainer> containers = new ArrayList<TempContainer>();
        containers.add(new TempContainer<>(indexFirstList));
        containers.add(new TempContainer<>(indexSecondList));
        List<ArrayList<Integer>> combinations = getCombination(0, containers);
        for (ArrayList<Integer> indices: combinations) {

            ArrayList<Integer> move = new ArrayList<Integer>();
            // form move from indices
            for (int index: indices) {
                if (index >= firstList.size()) {
                    move.addAll(secondList.get(index));
                } else {
                    move.addAll(firstList.get(index));
                }
            }
            // don't test move if it uses the same double/triple value.
            Set<Integer> set = new HashSet<Integer>(move);
            if (set.size() == 5 && g.isBigger(move)) {
                nextMoves.add(move);
            }
        }
    }
    // https://stackoverflow.com/questions/22632826/combination-of-elements-of-multiple-arrays
    public List<ArrayList<Integer>> getCombination(int currentIndex, List<TempContainer> containers) {
        if (currentIndex == containers.size()) {
            // Skip the items for the last container
            List<ArrayList<Integer>> combinations = new ArrayList<ArrayList<Integer>>();
            combinations.add(new ArrayList<Integer>());
            return combinations;
        }
        List<ArrayList<Integer>> combinations = new ArrayList<ArrayList<Integer>>();
        TempContainer<Integer> container = containers.get(currentIndex);
        List<Integer> containerItemList = container.getItems();
        // Get combination from next index
        List<ArrayList<Integer>> suffixList = getCombination(currentIndex + 1, containers);
        int size = containerItemList.size();
        for (int ii = 0; ii < size; ii++) {
            Integer containerItem = containerItemList.get(ii);
            if (suffixList != null) {
                for (List<Integer> suffix : suffixList) {
                    ArrayList<Integer> nextCombination = new ArrayList<Integer>();
                    nextCombination.add(containerItem);
                    nextCombination.addAll(suffix);
                    combinations.add(nextCombination);
                }
            }
        }
        return combinations;
    }

    // http://www.geeksforgeeks.org/print-all-possible-combinations-of-r-elements-in-a-given-array-of-size-n/
    private void getCombinations(Integer arr[], int n, int r, ArrayList<ArrayList<Integer>> nextMoves, Game g) {
        // A temporary array to store all combination one by one
        int data[] = new int[r];

        // Get all combination using temporary array 'data[]'
        ArrayList<ArrayList<Integer>> combinations = new ArrayList<ArrayList<Integer>>();
        combinationUtil(arr, n, r, 0, data, 0, combinations);

        for (ArrayList<Integer> move: combinations) {
            if (g.isBigger(move)) {
                nextMoves.add(move);
            }
        }
    }

    /* arr[]  ---> Input Array
       n      ---> Size of input array
       r      ---> Size of a combination to be printed
       index  ---> Current index in data[]
       data[] ---> Temporary array to store current combination
       i      ---> index of current element in arr[]     */
    private void combinationUtil(Integer arr[], int n, int r, int index, int data[], int i, ArrayList<ArrayList<Integer>> combinations) {
        // Current combination is ready, add it to list
        if (index == r) {
            ArrayList<Integer> newMove = new ArrayList<Integer>(0);
            for (int j=0; j<r; j++)
                newMove.add(data[j]);

            combinations.add(newMove);
            return;
        }

        // When no more elements are there to put in data[]
        if (i >= n)
            return;

        // current is included, put next at next location
        data[index] = arr[i];
        combinationUtil(arr, n, r, index+1, data, i+1, combinations);

        // current is excluded, replace it with next (Note that
        // i+1 is passed, but index is not changed)
        combinationUtil(arr, n, r, index, data, i+1, combinations);
    }
}
