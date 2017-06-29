package com.example.louis.pp;

import java.util.ArrayList;

/**
 * Created by louis on 27/06/2017.
 */
public class Node {
    private int heuristic;
    private ArrayList<Node> children;


    public boolean isTerminalNode() {
        return children.size() == 0;
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Node> children) {
        this.children = children;
    }

    public int getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(int heuristic) {
        this.heuristic = heuristic;
    }
}
