package edu.fandm.mobapp.worldy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Main extends AppCompatActivity {

    public String[] word_dict = {"BEAR", "BORE", "DARE", "DOOR", "CHORE", "AIR", "CARE", "WINE", "BEER", "WIND", "FIND", "BIND"};

    public String[] search_for_match(String word1, String word2, int x) {
        String[] result;
        if (word1.length() != word2.length()) {
        return null;
        }
        result = find_matches();
        return null;
    }

    public String[] find_matches() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}