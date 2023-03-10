package edu.fandm.mobapp.worldy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class ConfigurationScreen extends AppCompatActivity {

    private final static String TAG = ConfigurationScreen.class.getName();
    int difficulty = 0;
    Graph graph = new Graph();

    public List<String> finalPath;

    List<List<String>> easyPaths = new ArrayList<List<String>>();
    List<List<String>> mediumPaths = new ArrayList<List<String>>();
    List<List<String>> hardPaths = new ArrayList<List<String>>();

    EditText start_word;
    EditText end_word;
    SeekBar diff_slider;
    Button random_puzzle;
    Button play_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_screen);
        start_word = findViewById(R.id.startWord);
        end_word = findViewById(R.id.endWord);
        diff_slider = findViewById(R.id.DifficutlySelector);
        random_puzzle = findViewById(R.id.randPuzzle);
        play_button = findViewById(R.id.playButton);

        showWorking(true);
        dpcExecutor dpcE = new dpcExecutor();
        dpcE.execute(dpc);

        diff_slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                difficulty = i;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        play_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (start_word.getText().toString().equals("") || end_word.getText().toString().equals("")){
                    Toast.makeText(ConfigurationScreen.this, "Please enter a start and end word", Toast.LENGTH_SHORT).show();
                }
                else{
                    String start = start_word.getText().toString();
                    String end = end_word.getText().toString();
                    List<String> path = graph.getPath(start, end);
                    if (path == null){
                        Toast.makeText(ConfigurationScreen.this, "No path found", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        finalPath = path;
                        Intent intent = new Intent(ConfigurationScreen.this, GameScreen.class);
                        startActivity(intent);
                    }
                }
            }
        });

        random_puzzle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call different paths function, based of difficulty from slider pick a random path from corrresponding list
                //set start and end words to the path

                Random rand = new Random();
                int randInt;
                if (difficulty == 0){
                    randInt = rand.nextInt(easyPaths.size());
                    System.out.println("Easy");
                    start_word.setText(easyPaths.get(randInt).get(0));
                    end_word.setText(easyPaths.get(randInt).get(easyPaths.get(randInt).size()-1));
                }
                else if (difficulty == 1){
                    randInt = rand.nextInt(mediumPaths.size());
                    System.out.println("Medium");
                    start_word.setText(mediumPaths.get(randInt).get(0));
                    end_word.setText(mediumPaths.get(randInt).get(mediumPaths.get(randInt).size()-1));
                }
                else if (difficulty == 2){
                    randInt = rand.nextInt(hardPaths.size());
                    System.out.println("Hard");
                    start_word.setText(hardPaths.get(randInt).get(0));
                    end_word.setText(hardPaths.get(randInt).get(hardPaths.get(randInt).size()-1));
                }

            }
        });

    }

    interface differentPathsCallback{
        void onPathsFound(List<List<String>> easyPaths, List<List<String>> mediumPaths, List<List<String>> hardPaths);
    }

    differentPathsCallback dpc = new differentPathsCallback() {
        @Override
        public void onPathsFound(List<List<String>> easyPaths, List<List<String>> mediumPaths, List<List<String>> hardPaths) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showWorking(false);
                    Toast.makeText(ConfigurationScreen.this, "Paths Found", Toast.LENGTH_SHORT).show();

                }
            });
        }
    };

    public class dpcExecutor{
        public void execute(final differentPathsCallback dpc){
            ExecutorService es = Executors.newFixedThreadPool(20);
            es.execute(new Runnable() {
                @Override
                public void run() {
                    differentPaths();
                    dpc.onPathsFound(easyPaths, mediumPaths, hardPaths);
                }
            });

        }
    }

    private void showWorking(boolean on) {
        Log.d(TAG, "working...");
        View v = findViewById(R.id.working);
        if (on) {
            v.setVisibility(View.VISIBLE);
            Animation a = AnimationUtils.loadAnimation(this, R.anim.blink);
            v.setAnimation(a);
            v.animate();

        } else {
            v.setVisibility(View.INVISIBLE);
            v.clearAnimation();
        }
    }

    private void differentPaths(){
        Map<Integer, String> wordMap = graph.getWordDict();
        for (int i = 0; i < wordMap.size()/10; i++){
            for (int j = 0; j < wordMap.size()/50; j++){
                if (i != j) {
                    List<String> path = graph.getPath(wordMap.get(i), wordMap.get(j));
                    if (path != null && path.size() > 2 && path.size() < 6) {
                        System.out.println(path);
                        switch (path.size()) {
                            case 3:
                                easyPaths.add(path);
                                break;
                            case 4:
                                mediumPaths.add(path);
                                break;
                            case 5:
                                hardPaths.add(path);
                                break;
                        }
                    }
                }
            }
        }
    }


}