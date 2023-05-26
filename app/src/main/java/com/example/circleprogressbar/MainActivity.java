package com.example.circleprogressbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView tv = findViewById(R.id.sample_text);
        final CircleProgressBar cpbView = findViewById(R.id.cpbView);
        final CircleProgressBar cpbView1 = findViewById(R.id.cpbView1);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cpbView.setProgress(95.6f,true);
                cpbView1.setProgress(95.6f,true);
            }
        });
    }
}