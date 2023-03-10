package edu.fandm.mobapp.worldy;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class GameScreen extends AppCompatActivity {

    public String word_source; // assign this to whatever you need to get
    public String TAG = "GameScreen";
    public interface GetJSONCallback {
        void onComplete(String json);
    }

    public interface GetImageCallback {
        void onComplete(Bitmap bp);
    }

    public GameScreen.GetJSONCallback gjsonc = new GameScreen.GetJSONCallback() {
        @Override
        public void onComplete(String output) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (output != null) {
                        GameScreen.GetImageExecutor gimge = new GameScreen.GetImageExecutor();
                        gimge.fetch(output, gimgc);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Could not get JSON file!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };

    public GameScreen.GetImageCallback gimgc = new GameScreen.GetImageCallback() {
        @Override
        public void onComplete(Bitmap output) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (output != null) {
                        //ImageView iv = findViewById(R.id.test_image);
                        //iv.setImageBitmap(output);
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
            URL url = new URL("https://pixabay.com/api/?key=34155944-44bc9eeb96f0cc3d13b404eaa&q=" + word_source + "&image_type=photo&editors_choice=true");

            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer data = new StringBuffer();
            String curr;
            while ((curr = in.readLine()) != null) {
                data.append(curr);
            }
            Random r_idx = new Random();
            JSONObject jsonCatFact = new JSONObject(data.toString());
            JSONArray jsonArray = jsonCatFact.getJSONArray("hits");
            JSONObject help = (JSONObject)jsonArray.get(r_idx.nextInt(4));
            ret_url = (String)help.get("userImageURL");

        }
        catch (MalformedURLException mue) {
            Looper.prepare();
            Toast.makeText(getApplicationContext(), "Invalid URL!", Toast.LENGTH_SHORT).show();
        }
        catch (JSONException jsone) {
            Looper.prepare();
            Toast.makeText(getApplicationContext(), "JSON file formatted improperly!", Toast.LENGTH_SHORT).show();
            jsone.printStackTrace();
        }
        catch (IOException ioe) {
            Looper.prepare();
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
        public void fetch(final GameScreen.GetJSONCallback callback) {
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
        public void fetch(final String src, final GameScreen.GetImageCallback callback) {
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


    public void getImage() {
        for (int i = 0; i < path.size(); i++) {
            String word = path.get(i);

    }


}}