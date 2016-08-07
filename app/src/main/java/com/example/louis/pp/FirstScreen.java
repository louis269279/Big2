package com.example.louis.pp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

public class FirstScreen extends Activity {

    int[] playerType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);
        playerType = new int[4];
        View.OnFocusChangeListener ofcl = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        };
        findViewById(R.id.name1).setOnFocusChangeListener(ofcl);
        findViewById(R.id.name2).setOnFocusChangeListener(ofcl);
        findViewById(R.id.name3).setOnFocusChangeListener(ofcl);
        findViewById(R.id.name4).setOnFocusChangeListener(ofcl);
    }

    public void startGame (View view) {

        if (playerType[0] != 0 && playerType[1] != 0 &&
                playerType[2] != 0 && playerType[3] != 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Error!")
                    .setMessage("At least one person must be a player!")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            EditText name1 = (EditText) findViewById(R.id.name1);
            EditText name2 = (EditText) findViewById(R.id.name2);
            EditText name3 = (EditText) findViewById(R.id.name3);
            EditText name4 = (EditText) findViewById(R.id.name4);

            Intent intent = new Intent(FirstScreen.this, MainActivity.class);

            intent.putExtra("name1", name1.getText().toString());
            intent.putExtra("name2", name2.getText().toString());
            intent.putExtra("name3", name3.getText().toString());
            intent.putExtra("name4", name4.getText().toString());

            intent.putExtra("type1", playerType[0]);
            intent.putExtra("type2", playerType[1]);
            intent.putExtra("type3", playerType[2]);
            intent.putExtra("type4", playerType[3]);

            startActivity(intent);
        }
    }

    public void changePlayerType (View view) {
        switch (view.getId()) {
            case R.id.playerType0:
                playerType[0] = (playerType[0]+1)%4;
                changeIcon((ImageButton) view, playerType[0]);
                break;
            case R.id.playerType1:
                playerType[1] = (playerType[1]+1)%4;
                changeIcon((ImageButton) view, playerType[1]);
                break;
            case R.id.playerType2:
                playerType[2] = (playerType[2]+1)%4;
                changeIcon((ImageButton) view, playerType[2]);
                break;
            case R.id.playerType3:
                playerType[3] = (playerType[3]+1)%4;
                changeIcon((ImageButton) view, playerType[3]);
                break;
        }
    }

    private void changeIcon(ImageButton ib, int type) {
        if (type == 0) {
            ib.setImageResource(R.drawable.player);
        } else if (type == 1) {
            ib.setImageResource(R.drawable.easy);
        } else if (type == 2) {
            ib.setImageResource(R.drawable.med);
        } else {
            ib.setImageResource(R.drawable.hard);
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
