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
        if (word_dict != null && word_dict.length != 0) {

        }
        result = find_matches(word_dict, word1, word2, 0, word1.length());
        return result;
    }

    public String[] find_matches(String[] list, String curr, String dest, int try_num, int try_max) {
        if (try_num == try_max) {
            return null;
        }
        for (String entry : list) {
            int correct = 0;
            for (int i = 0; i < curr.length(); i++) {
                if (entry.substring(i,i) == curr.substring(i,i)) {
                    correct++;
                }
            }
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}