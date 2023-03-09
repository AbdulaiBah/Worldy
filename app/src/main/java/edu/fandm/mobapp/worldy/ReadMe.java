package edu.fandm.mobapp.worldy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class ReadMe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_me);
    }

    @Override
    public void onUserInteraction() {
        Intent i = new Intent(getApplicationContext(), ConfigurationScreen.class);
        startActivity(i);
    }

}