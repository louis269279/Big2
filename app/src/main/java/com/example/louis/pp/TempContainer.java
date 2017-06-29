package com.example.louis.pp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by louis on 29/06/2017.
 */
public class TempContainer<T> {
    private List<T> items;

    public TempContainer(List<T> items) {
        this.items = items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public List<T> getItems() {
        return items;
    }
}
