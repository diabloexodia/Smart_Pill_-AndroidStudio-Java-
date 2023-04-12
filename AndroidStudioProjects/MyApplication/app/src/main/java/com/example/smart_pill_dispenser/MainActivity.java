package com.example.smart_pill_dispenser;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EditText nameEditText = findViewById(R.id.name);
        EditText dosageEditText = findViewById(R.id.dosage);
        EditText timeEditText = findViewById(R.id.time);
        Button submitButton = findViewById(R.id.button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "yo", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
