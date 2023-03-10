package edu.fandm.mobapp.worldy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
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
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class ConfigurationScreen extends AppCompatActivity {

    public int mode;
    public String TAG = "Configuration Screen";
    public String word_source = "cats";
    public ArrayList<String> word_dict;
    public static String fileName;

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

    public ArrayList<String> populate() {
        // Use this to generate the ArrayList for search_for_match
        String[] dummy = {"that", "this", "with", "from", "your", "have", "more", "will", "home", "page", "free", "time", "they", "site", "what", "news", "only", "when", "here", "also", "help", "view", "been", "were", "some", "like", "than", "find", "date", "back", "list", "name", "just", "over", "year", "into", "next", "used", "work", "last", "most", "data", "make", "them", "post", "city", "such", "best", "then", "good", "well", "info", "high", "each", "very", "book", "read", "need", "many", "user", "said", "does", "mail", "full", "life", "know", "days", "part", "real", "item", "ebay", "must", "made", "line", "send", "type", "take", "area", "want", "long", "code", "show", "even", "much", "sign", "file", "link", "open", "case", "same", "both", "game", "care", "down", "size", "shop", "text", "rate", "form", "love", "john", "main", "call", "save", "york", "card", "jobs", "food", "sale", "teen", "room", "join", "west", "look", "left", "team", "week", "note", "live", "june", "plan", "cost", "july", "test", "come", "cart", "play", "less", "blog", "park", "side", "give", "sell", "body", "east", "club", "road", "gift", "hard", "four", "blue", "easy", "star", "hand", "keep", "baby", "term", "film", "head", "cell", "self", "away", "once", "sure", "cars", "tell", "able", "gold", "arts", "past", "five", "upon", "says", "land", "done", "ever", "word", "bill", "talk", "kids", "true", "else", "mark", "rock", "tips", "plus", "auto", "edit", "fast", "fact", "unit", "tech", "meet", "feel", "bank", "risk", "town", "girl", "toys", "golf", "loan", "wide", "sort", "half", "step", "none", "paul", "lake", "sony", "fire", "chat", "html", "loss", "face", "base", "near", "stay", "turn", "mean", "king", "copy", "drug", "pics", "cash", "seen", "port", "stop", "soon", "held", "mind", "lost", "tour", "menu", "hope", "wish", "role", "came", "fine", "hour", "bush", "huge", "kind", "move", "logo", "nice", "sent", "band", "lead", "went", "mode", "fund", "male", "took", "song", "cnet", "late", "fall", "idea", "tool", "hill", "maps", "deal", "hold", "safe", "feed", "hall", "anti", "ship", "paid", "hair", "tree", "thus", "wall", "wine", "vote", "ways", "rule", "told", "feet", "door", "cool", "asia", "uses", "java", "pass", "fees", "skin", "prev", "mary", "ring", "iraq", "boys", "deep", "rest", "pool", "mini", "fish", "pack", "born", "race", "debt", "core", "sets", "wood", "rent", "dark", "host", "isbn", "fair", "ohio", "gets", "dead", "mike", "trip", "poor", "eyes", "farm", "lord", "hear", "goes", "wife", "hits", "zone", "jack", "flat", "flow", "path", "laws", "skip", "diet", "army", "gear", "lots", "firm", "jump", "dvds", "ball", "goal", "sold", "wind", "palm", "pain", "xbox", "oral", "ford", "edge", "root", "pink", "shot", "cold", "foot", "mass", "heat", "wild", "miss", "task", "soft", "fuel", "walk", "wait", "rose", "pick", "load", "tags", "guys", "drop", "rich", "ipod", "seem", "hire", "gave", "ones", "rank", "kong", "died", "inch", "snow", "camp", "fill", "gone", "fort", "gene", "disc", "boat", "icon", "ends", "cast", "felt", "soul", "aids", "flag", "atom", "iron", "void", "disk", "desk", "dave", "hong", "vice", "duty", "bear", "gain", "lack", "iowa", "knew", "zoom", "blow", "clip", "wire", "tape", "spam", "acid", "cent", "null", "zero", "roll", "bath", "font", "beta", "fail", "jazz", "bags", "wear", "rare", "bars", "dual", "rise", "bird", "lady", "fans", "dell", "seat", "bids", "toll", "cape", "mine", "whom", "math", "dogs", "moon", "fear", "wars", "kept", "beat", "arms", "utah", "hide", "slow", "faqs", "nine", "eric", "spot", "grow", "rain", "onto", "diff", "bass", "hole", "pets", "ride", "pair", "runs", "yeah", "evil", "euro", "peak", "salt", "bell", "jeff", "lane", "kill", "ages", "plug", "cook", "perl", "bike", "lose", "seek", "tony", "kits", "soil", "matt", "exit", "iran", "keys", "wave", "holy", "acts", "mesh", "dean", "poll", "unix", "bond", "jean", "visa", "pure", "lens", "draw", "warm", "babe", "crew", "legs", "rear", "node", "lock", "mile", "mens", "bowl", "tank", "navy", "dish", "adam", "slot", "gray", "demo", "hate", "rice", "loop", "gary", "vary", "rome", "arab", "milk", "boot", "push", "misc", "dear", "beer", "jose", "jane", "earn", "twin", "dont", "bits", "suit", "chip", "char", "echo", "grid", "voip", "pull", "nasa", "nick", "plot", "pump", "anne", "exam", "ryan", "beds", "bold", "scan", "aged", "bulk", "pmid", "cute", "para", "seed", "peer", "meat", "alex", "bang", "bone", "bugs", "gate", "tone", "busy", "neck", "wing", "tiny", "rail", "tube", "belt", "luck", "dial", "gang", "cake", "semi", "andy", "cafe", "till", "shoe", "sand", "seal", "lies", "pipe", "deck", "thin", "sick", "dose", "lets", "cats", "greg", "folk", "okay", "hist", "lift", "lisa", "mall", "fell", "yard", "pour", "tion", "dust", "wiki", "kent", "adds", "ward", "roof", "kiss", "rush", "mpeg", "yoga", "lamp", "rico", "http", "glad", "wins", "rack", "boss", "solo", "tall", "pdas", "nova", "wake", "drum", "foto", "ease", "tabs", "pine", "tend", "gulf", "rick", "hunt", "thai", "fred", "mill", "burn", "labs", "sole", "laid", "clay", "weak", "blvd", "wise", "odds", "marc", "sons", "leaf", "cuba", "silk", "kate", "wolf", "fits", "kick", "meal", "hurt", "slip", "cuts", "mars", "caps", "pill", "meta", "mint", "spin", "wash", "aims", "ieee", "corp", "soap", "axis", "guns", "hero", "punk", "duke", "pace", "wage", "dawn", "carl", "coat", "rica", "doll", "peru", "nike", "reed", "mice", "temp", "vast", "wrap", "mood", "quiz", "beam", "tops", "shut", "ncaa", "thou", "mask", "coal", "lion", "goto", "neil", "beef", "hats", "surf", "hook", "cord", "crop", "lite", "sing", "tons", "hang", "hood", "fame", "eggs", "ruby", "stem", "drew", "tune", "corn", "puts", "grew", "trek", "ties", "brad", "jury", "tail", "lawn", "soup", "byte", "nose", "oclc", "juan", "thru", "jews", "trim", "espn", "quit", "lung", "todd", "doug", "sees", "bull", "cole", "mart", "tale", "lynn", "docs", "coin", "fake", "cure", "arch", "hdtv", "asin", "bomb", "harm", "deer", "oven", "noon", "cams", "joel", "proc", "mate", "chef", "isle", "slim", "luke", "comp", "pete", "spec", "penn", "midi", "tied", "dale", "oils", "sept", "unto", "pays", "lang", "stud", "fold", "pole", "mega", "bend", "moms", "glen", "lips", "pond", "tire", "chad", "josh", "drag", "ripe", "rely", "scsi", "nuts", "nail", "span", "joke", "univ", "pads", "inns", "cups", "foam", "poem", "asks", "bean", "bias", "swim", "nano", "loud", "rats", "stat", "cruz", "bios", "thee", "ruth", "pray", "pope", "jeep", "bare", "hung", "mono", "tile", "apps", "ciao", "knee", "prep", "chem", "pros", "cant", "sara", "joan", "duck", "dive", "fiji", "audi", "raid", "volt", "dirt", "acer", "dist", "geek", "sink", "grip", "watt", "pins", "reno", "polo", "horn", "prot", "frog", "logs", "snap", "jpeg", "swap", "flip", "buzz", "nuke", "boom", "calm", "fork", "zope", "gmbh", "sims", "tray", "sage", "suse", "cave", "wool", "eyed", "grab", "oops", "trap", "fool", "karl", "dies", "jail", "ipaq", "comm", "lace", "ugly", "hart", "ment", "biol", "rows", "treo", "gods", "poly", "ears", "fist", "mere", "cons", "taxi", "worn", "shaw", "expo", "deny", "bali", "judy", "trio", "cube", "rugs", "fate", "gras", "oval", "soma", "href", "benz", "wifi", "tier", "earl", "guam", "cite", "mess", "rope", "dump", "hose", "pubs", "mild", "clan", "sync", "mesa", "hull", "shed", "memo", "tide", "funk", "reel", "bind", "rand", "buck", "usgs", "acre", "lows", "aqua", "chen", "emma", "pest", "reef", "chan", "beth", "jill", "sofa", "dans", "viii", "tent", "dept", "hack", "dare", "hawk", "lamb", "junk", "lucy", "hans", "poet", "epic", "sake", "sans", "lean", "dude", "luis", "alto", "gore", "cult", "dash", "cage", "divx", "hugh", "jake", "eval", "ping", "flux", "muze", "oman", "rage", "adsl", "prix", "avon", "rays", "walt", "acne", "libs", "undo", "dana", "halo", "gays", "exec", "maui", "vids", "yale", "doom", "owen", "bite", "issn", "myth", "weed", "oecd", "dice", "quad", "dock", "mods", "hint", "msie", "buys", "pork", "barn", "fare", "asus", "bald", "fuji", "leon", "mold", "dame", "herb", "alot", "idle", "cove", "casa", "eden", "incl", "reid", "flex", "rosa", "hash", "lazy", "carb", "pens", "worm", "deaf", "mats", "blah", "mime", "feof", "usda", "keen", "peas", "urls", "owns", "zinc", "guru", "levy", "grad", "bras", "kyle", "pale", "gaps", "tear", "nest", "nato", "gale", "stan", "idol", "moss", "cork", "mali", "dome", "heel", "yang", "dumb", "feat", "ntsc", "usps", "conf", "glow", "oaks", "erik", "paso", "norm", "ware", "jade", "foul", "keno", "seas", "pose", "mrna", "goat", "sail", "sega", "cdna", "bolt", "gage", "urge", "smtp", "kurt", "neon", "ours", "lone", "cope", "lime", "kirk", "bool", "spas", "jets", "intl", "yarn", "knit", "pike", "hugo", "gzip", "ctrl", "bent", "laos"};
        return new ArrayList<String>(Arrays.asList(dummy));
    }

    public ArrayList<String> search_for_match(ArrayList<String> list, String word1, String word2, int x) {
        ArrayList<String> result;
        if (word1.length() != word2.length()) {
            return null;
        }
        ArrayList<String> output_of = new ArrayList<String>();
        output_of.add(word1);
        result = find_matches(list, word1, word2, output_of, 0, 16);
        return result;
    }

    public int hamming(String w1, String w2) {
        int correct = 0;
        for (int i = 0; i < w1.length(); i++) {
            correct += (w1.charAt(i) == w2.charAt(i)) ? 1 : 0;
        }
        return correct;
    }


    public ArrayList<String> find_matches(ArrayList<String> list, String curr, String dest, ArrayList<String> output_list, int try_num, int try_max) {
        if (try_num == try_max) {
            return null;
        }
        Log.d("ConfigurationScreen", curr);
        if (curr.equals(dest)) {
            return output_list;
        }
        ArrayList<String> matches = new ArrayList<String>();
        for (String entry : list) {
            if (entry.length() != 0) {
                int correct = hamming(entry,curr);
                if (correct == entry.length() - 1) {
                    Log.d("ConfigurationScreen",entry + " + " + curr + ": " + correct);
                    output_list.add(entry);
                    ArrayList<String> temp = (ArrayList<String>) list.clone();
                    temp.remove(entry);
                    return find_matches(temp, entry, dest, output_list, try_num+1, entry.length());
                }
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
        //ImageView testImage = findViewById(R.id.test_image);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.INTERNET},0);

        /*
        testImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GetJSONExecutor jse = new GetJSONExecutor();
                jse.fetch(gjsonc);

            }
        });
        */

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
                    ArrayList<String> word_list = populate();
                    //ArrayList<String> word_list = scanFile(fileName, from, to);
                    ArrayList<String> matches = search_for_match(word_list, from, to, from.length());
                    if (matches != null) {
                        // matches is the final list!
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Could not find matches!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        random_puzzle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String[]> outcomes = new ArrayList<String[]>();
                outcomes.add(new String[]{"live", "line"});
                outcomes.add(new String[]{"bear", "hear"});
                outcomes.add(new String[]{"char", "what"});
                outcomes.add(new String[]{"beer", "then"});
                outcomes.add(new String[]{"prod", "prot"});
                outcomes.add(new String[]{"rage", "face"});
                outcomes.add(new String[]{"cage", "face"});
                outcomes.add(new String[]{"aims", "bids"});
                outcomes.add(new String[]{"bids", "aids"});
                outcomes.add(new String[]{"wine", "life"});
                outcomes.add(new String[]{"noon", "mood"});
                outcomes.add(new String[]{"mood", "wood"});
                outcomes.add(new String[]{"moon", "soon"});
                outcomes.add(new String[]{"tent", "nest"});
                outcomes.add(new String[]{"read", "road"});
                outcomes.add(new String[]{"rain", "fail"});
                Random random = new Random();
                int r_idx = random.nextInt(outcomes.size());
                start_word.setText(outcomes.get(r_idx)[0]);
                end_word.setText(outcomes.get(r_idx)[1]);
                // I put the words here for the sake of clarity,
                // but the player wouldn't see these
            }
        });

    }
}