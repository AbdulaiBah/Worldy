package edu.fandm.mobapp.worldy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

import kotlin.io.LineReader;

public class GameScreen extends AppCompatActivity {

    public String word_source; // assign this to whatever you need to get
    public String TAG = "GameScreen";

    public void hintButton(View view) {
        Random rand = new Random();
        int rand_int = rand.nextInt(path.size());
        String hint = path.get(rand_int);
        while (hint.equals(path.get(0)) || hint.equals(path.get(path.size() - 1))){
            rand_int = rand.nextInt(path.size());
            hint = path.get(rand_int);
        }
        Toast.makeText(getApplicationContext(), "Hint: " + hint, Toast.LENGTH_SHORT).show();
    }

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
                        ImageView iv = findViewById(R.id.puzzleImage);
                        iv.setImageBitmap(output);
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
            JSONObject help = (JSONObject)jsonArray.get(r_idx.nextInt(10));
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
            Looper.prepare();
            Toast.makeText(getApplicationContext(), "Invalid URL!", Toast.LENGTH_SHORT).show();
        }
        catch (IOException ioe) {
            Looper.prepare();
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

    public void updateImage() {
        GameScreen.GetJSONExecutor gjse = new GameScreen.GetJSONExecutor();
        gjse.fetch(gjsonc);
    }
    List<String> path = ConfigurationScreen.finalPath;
    LinearLayout ll;

    int CorrectGuesses = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.INTERNET},0);

        ll = findViewById(R.id.guesses);

        initDisplay();

    }

    private void initDisplay() {
        TextView startView = findViewById(R.id.startText);
        startView.setText(path.get(0));
        TextView endView = findViewById(R.id.endText);
        endView.setText(path.get(path.size()-1));

        for (int i = 0; i < path.size()-2; i++) {
            EditText et = new EditText(this);
            LinearLayout.LayoutParams params;
            et.setLayoutParams(params = new LinearLayout.LayoutParams(
                    100,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
            params.rightMargin = (int) (2 * getResources().getDisplayMetrics().density + 0.5f);
            params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
            params.width = 100;
            params.weight = 1;
            et.setWidth(100);
            et.setId(i);
            et.setFontFeatureSettings("sans-serif-black");
            et.setTextSize(12);
            et.setTextColor(Color.parseColor("#F5F1ED"));
            et.setBackgroundColor(Color.parseColor("#2C2C2C"));
            et.setPadding(10,10,10,10);
            et.setGravity(Gravity.CENTER_HORIZONTAL| Gravity.CENTER_VERTICAL);
            et.setAllCaps(true);
            et.setLayoutParams(params);

            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    int index = ll.indexOfChild(et);
                    word_source = path.get(index);
                    updateImage();
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (checkWord(et,s.toString())) {
                        et.setEnabled(false);
                        et.setBackgroundColor(Color.parseColor("#20BA79"));
                        CorrectGuesses++;
                        if (CorrectGuesses == path.size()-2) {
                            finishGame();
                        }
                    }
                    else {
                        et.setBackgroundColor(Color.parseColor("#A91B0D"));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Do nothing
                }
            });

            int index = ll.indexOfChild(endView);
            ll.addView(et, index);
        }
    }

    public boolean checkWord(EditText editText, String guess) {
        int index = ll.indexOfChild(editText);
        System.out.println(index);
        String userguess = guess.toLowerCase();
        System.out.println("ans " + path.get(index));
        if (!userguess.isEmpty() && userguess.equals(path.get(index))) {
            Toast.makeText(getApplicationContext(), "Correct guess!", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (!userguess.isEmpty() && !userguess.equals(path.get(index))){
            // Don't show error message when the user hasn't entered anything yet
            return false;
        }
        return false;
    }

    private void rotateAnimation(View v){
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotate_animation);
        rotate.setDuration(1000);
        rotate.setRepeatCount(Animation.INFINITE);
        v.startAnimation(rotate);
    }

    public void finishGame() {
        ImageView iv = findViewById(R.id.star);
        iv.setVisibility(View.VISIBLE);
        rotateAnimation(iv);
        Toast.makeText(getApplicationContext(), "You win!", Toast.LENGTH_SHORT).show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), ConfigurationScreen.class);
                startActivity(intent);
            }
        }, 3000); // 3000 milliseconds = 3 seconds
    }



}