package edu.fandm.mobapp.worldy;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class ConfigurationScreen extends AppCompatActivity {

    public int mode;
    public String TAG = "Configuration Screen";
    public String word_source = "bear";
    public ArrayList<String> word_dict;
    public static String fileName = "app/java/edu.fandm.mobapp.worldly/words_simple.txt";

    public interface GetJSONCallback {
        void onComplete(String json);
    }

    public interface GetImageCallback {
        void onComplete(Bitmap bp);
    }

    public ConfigurationScreen.GetJSONCallback gjsonc = new ConfigurationScreen.GetJSONCallback() {
        @Override
        public void onComplete(String output) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (output != null) {
                        ConfigurationScreen.GetImageExecutor gimge = new ConfigurationScreen.GetImageExecutor();
                        gimge.fetch(output, gimgc);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Could not get JSON file!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };

    public ConfigurationScreen.GetImageCallback gimgc = new ConfigurationScreen.GetImageCallback() {
        @Override
        public void onComplete(Bitmap output) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (output != null) {
                        // From here, set image to be equal to whatever gets fetched
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Could not get JSON file!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };

    public String getURL() {
        String ret_url = null;
        try {
            // Example URL: https://pixabay.com/api/?key=34155944-44bc9eeb96f0cc3d13b404eaa&q=yellow+flowers&image_type=photo
            URL url = new URL("https://pixabay.com/api/?key=34155944-44bc9eeb96f0cc3d13b404eaa&q=" + word_source + "&image_type=photo");

            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer data = new StringBuffer();
            String curr;
            while ((curr = in.readLine()) != null) {
                data.append(curr);
            }
            JSONObject jsonCatFact = new JSONObject(data.toString());
            ret_url = (String)jsonCatFact.get("imageURL");

        }
        catch (MalformedURLException mue) {
            Toast.makeText(getApplicationContext(), "Invalid URL!", Toast.LENGTH_SHORT).show();
        }
        catch (JSONException jsone) {
            Toast.makeText(getApplicationContext(), "JSON file formatted improperly!", Toast.LENGTH_SHORT).show();
        }
        catch (IOException ioe) {
            Toast.makeText(getApplicationContext(), "URL cannot be connected!", Toast.LENGTH_SHORT).show();
        }
        return ret_url;
    }

    public Bitmap getImage(String src) {
        Bitmap bp = null;
        try {
            URL url = new URL(src);

            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.connect();

            InputStream in = new BufferedInputStream(url.openStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            byte[] buf = new byte[1024];
            int n = 0;
            while (-1!=(n=in.read(buf))) {
                out.write(buf,0,n);
            }
            out.close();
            in.close();

            byte[] bytemap = out.toByteArray();
            bp = BitmapFactory.decodeByteArray(bytemap,0,bytemap.length);

            Log.d(TAG,"Finished downloading image!");

        }
        catch (MalformedURLException mue) {
            Toast.makeText(getApplicationContext(), "Invalid URL!", Toast.LENGTH_SHORT).show();
        }
        catch (IOException ioe) {
            Toast.makeText(getApplicationContext(), "URL cannot be connected.", Toast.LENGTH_SHORT).show();
        }
        return bp;
    }

    public class GetJSONExecutor {
        public void fetch(final ConfigurationScreen.GetJSONCallback callback) {
            ExecutorService es = Executors.newFixedThreadPool(1);
            es.execute(new Runnable() {
                @Override
                public void run() {
                    String url = getURL();
                    callback.onComplete(url);
                }
            });
        }
    }

    public class GetImageExecutor {
        public void fetch(final String src, final ConfigurationScreen.GetImageCallback callback) {
            ExecutorService es = Executors.newFixedThreadPool(1);
            es.execute(new Runnable() {
                @Override
                public void run() {
                    Bitmap bp = getImage(src);
                    callback.onComplete(bp);
                }
            });
        }
    }

    public ArrayList<String> scanFile(String file, String word1, String word2) {
        ArrayList<String> temp = new ArrayList<>();
        if (word1.length() != word2.length()) {
            return null;
        }
        try {
            Scanner scan = new Scanner(new File(file));
            while (scan.hasNextLine()) {
                String data = scan.nextLine();
                if (data.length() == word1.length() && (data.charAt(0) == word1.charAt(0) || data.charAt(0) == word2.charAt(0)) && !data.equals(word1)) {
                    temp.add(data.toLowerCase());
                }
            }
        }
        catch (FileNotFoundException fnfe) {
            Toast.makeText(getApplicationContext(), "File not found!", Toast.LENGTH_SHORT).show();
        }

        return temp;
    }
    public ArrayList<String> search_for_match(ArrayList<String> list, String word1, String word2, int x) {
        ArrayList<String> result;
        if (word1.length() != word2.length()) {
            return null;
        }
        ArrayList<String> output_list = new ArrayList<>(); output_list.add(word1);
        result = find_matches(list, word1, word2, output_list, 0, word1.length());
        return result;
    }

    public ArrayList<String> find_matches(ArrayList<String> list, String curr, String dest, ArrayList<String> output_list, int try_num, int try_max) {
        if (try_num == try_max) {
            return null;
        }
        if (curr.equals(dest)) {
            output_list.add(dest);
            return output_list;
        }
        for (String entry : list) {
            int correct = 0;
            for (int i = 0; i < curr.length(); i++) {
                correct += (entry.charAt(i) == curr.charAt(i) ? 1 : 0);
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
        setContentView(R.layout.activity_configuration_screen);
        EditText start_word = findViewById(R.id.startWord);
        EditText end_word = findViewById(R.id.endWord);
        SeekBar diff_slider = findViewById(R.id.DifficutlySelector);
        Button random_puzzle = findViewById(R.id.randPuzzle);
        Button play_button = findViewById(R.id.playButton);

        diff_slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mode = i;
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
                String from = start_word.getText().toString();
                String to = end_word.getText().toString();
                if (from.length() > 0 && to.length() > 0) {
                    ArrayList<String> word_list = scanFile(fileName, from, to);
                    ArrayList<String> matches = search_for_match(word_list, from, to, from.length());
                    Toast.makeText(getApplicationContext(), matches.size(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}