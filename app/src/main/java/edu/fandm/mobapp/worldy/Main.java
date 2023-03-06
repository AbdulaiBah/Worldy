package edu.fandm.mobapp.worldy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main extends AppCompatActivity {

    public ArrayList<String> word_dict;

    public static String fileName = "words_simple.txt";

    public ArrayList<String> scanFile(String file, String word1, String word2) {
        ArrayList<String> temp = new ArrayList<>();
        if (word1.length() != word2.length()) {
            return null;
        }
        try {
            Scanner scan = new Scanner(new File(file));
            while (scan.hasNextLine()) {
                String data = scan.nextLine();
                if (data.length() == word1.length() && (data.charAt(0) == word1.charAt(0) || data.charAt(0) == word2.charAt(0))) {
                    temp.add(data);
                }
            }
        }
        catch (FileNotFoundException fnfe) {
            Toast.makeText(getApplicationContext(), "File not found!", Toast.LENGTH_SHORT).show();
        }

        return temp;
    }
    public ArrayList<String> search_for_match(ArrayList<String> list, String word1, String word2, int x) {
        word_dict.addAll(list);
        ArrayList<String> result;
        if (word1.length() != word2.length()) {
        return null;
        }
        ArrayList<String> output_list = new ArrayList<>(); output_list.add(word1);
        result = find_matches(word_dict, word1, word2, output_list, 0, word1.length());
        return result;
    }

    public ArrayList<String> find_matches(ArrayList<String> list, String curr, String dest, ArrayList<String> output_list, int try_num, int try_max) {
        if (try_num == try_max) {
            return null;
        }
        if (curr.equals(dest)) {
            return output_list;
        }
        for (String entry : list) {
            int correct = 0;
            for (int i = 0; i < curr.length(); i++) {
                if (entry.charAt(i) == curr.charAt(i)) {
                    correct++;
                }
            }
            if (correct == curr.length() - 1) {
                ArrayList<String> temp = (ArrayList<String>)list.clone();
                temp.remove(curr);
                output_list.add(entry);
                return find_matches(temp,entry,dest,output_list,try_num+1,try_max);
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