package edu.fandm.mobapp.worldy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

public class GameScreen extends AppCompatActivity {

    ArrayList<String> path;
    String[] currentGuess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);


        currentGuess = new String[path.size()];
        String startWord = path.get(0);
        String endWord = path.get(path.size() - 1);
        currentGuess[0] = startWord;
        currentGuess[path.size() - 1] = endWord;



    }

    public void hintButton(View view) {

    }



    public boolean checkWord(String word) {
        int i = Arrays.asList(currentGuess).indexOf(word);
        return i == path.indexOf(word);
    }

    //Check that the word is a valid path in the graph

    public void getImage() {
        for (int i = 0; i < path.size(); i++) {
            String word = path.get(i);

    }


}