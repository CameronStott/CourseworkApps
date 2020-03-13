package com.example.locations_defib;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class first_screen extends AppCompatActivity {

    Button btnGetDirections;
    Button btnOrderNow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);


        btnGetDirections = findViewById(R.id.btnGetDirections);
        btnOrderNow = findViewById(R.id.btnOrderNow);

        btnGetDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent defibIntent = new Intent(first_screen.this, MainActivity.class);
                startActivity(defibIntent);
            }
        });

        btnOrderNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orderIntent = new Intent(getBaseContext(), LoginScreen.class);
                startActivity(orderIntent);
            }
        });
    }
}
